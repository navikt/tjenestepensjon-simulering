package no.nav.tjenestepensjon.simulering

import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_NAME
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_SIMULERING_MANGEL
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_SIMULERING_UFUL
import no.nav.tjenestepensjon.simulering.AsyncExecutor.AsyncResponse
import no.nav.tjenestepensjon.simulering.consumer.FindTpLeverandorCallable
import no.nav.tjenestepensjon.simulering.consumer.TpConfigConsumer
import no.nav.tjenestepensjon.simulering.consumer.TpRegisterConsumer
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor.EndpointImpl.SOAP
import no.nav.tjenestepensjon.simulering.exceptions.DuplicateStillingsprosentEndDateException
import no.nav.tjenestepensjon.simulering.exceptions.MissingStillingsprosentException
import no.nav.tjenestepensjon.simulering.exceptions.NoTpOrdningerFoundException
import no.nav.tjenestepensjon.simulering.exceptions.StillingsprosentCallableException
import no.nav.tjenestepensjon.simulering.model.v1.domain.FNR
import no.nav.tjenestepensjon.simulering.model.v1.domain.Stillingsprosent
import no.nav.tjenestepensjon.simulering.model.v1.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.model.v1.domain.Utbetalingsperiode
import no.nav.tjenestepensjon.simulering.model.v1.request.SimulerPensjonRequest
import no.nav.tjenestepensjon.simulering.model.v1.response.SimulertPensjon
import no.nav.tjenestepensjon.simulering.service.SimpleSimuleringService
import no.nav.tjenestepensjon.simulering.service.StillingsprosentResponse
import no.nav.tjenestepensjon.simulering.service.StillingsprosentService
import no.nav.tjenestepensjon.simulering.testHelper.anyNonNull
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDate.now
import java.util.Collections.singletonList
import java.util.concurrent.ExecutionException

@ExtendWith(MockitoExtension::class)
internal class SimpleSimuleringServiceTest {

    @Mock
    private lateinit var tpRegisterConsumer: TpRegisterConsumer

    @Mock
    private lateinit var asyncExecutor: AsyncExecutor<TpLeverandor, FindTpLeverandorCallable>

    @Mock
    private lateinit var stillingsprosentService: StillingsprosentService

    @Mock
    private lateinit var simuleringEnpointRouter: TjenestepensjonsimuleringEndpointRouter

    @Mock
    private lateinit var metrics: AppMetrics

    @Mock
    private lateinit var tpConfigConsumer: TpConfigConsumer

    @Mock
    private lateinit var tpLeverandorList: List<TpLeverandor>

    @InjectMocks
    private lateinit var simuleringService: SimpleSimuleringService

    private lateinit var request: SimulerPensjonRequest

    @BeforeEach
    fun beforeEach() {
        request = SimulerPensjonRequest(
                fnr = FNR("01011234567"),
                sivilstandkode = "ugift",
                simuleringsperioder = emptyList(),
                inntekter = emptyList()
        )
    }

    @Test
    @Throws(Exception::class)
    fun shouldReturnStatusAndFeilkodeWhenDuplicateStillingsprosentEndDate() {
        val tpOrdning = TPOrdning("1", "1")
        val tpLeverandor = TpLeverandor("lev1", "url1", SOAP)

        Mockito.`when`(tpRegisterConsumer.getTpOrdningerForPerson(anyNonNull())).thenReturn(listOf(tpOrdning))

        Mockito.`when`(stillingsprosentService.getStillingsprosentListe(anyNonNull(), anyNonNull())).thenReturn(StillingsprosentResponse(mapOf(tpOrdning to emptyList()), emptyList()))

        val asyncResponse = AsyncResponse<TPOrdning, TpLeverandor>().apply {
            resultMap[tpOrdning] = tpLeverandor
        }

        Mockito.`when`(asyncExecutor.executeAsync<TPOrdning>(anyNonNull())).thenReturn(asyncResponse)
        Mockito.`when`(stillingsprosentService.getLatestFromStillingsprosent(anyNonNull())).thenThrow(DuplicateStillingsprosentEndDateException("exception"))

        val simulertPensjon = simuleringService.simulerOffentligTjenestepensjon(request)
                .simulertPensjonListe.first()

        assertNotNull(simulertPensjon)
        assertEquals("FEIL", simulertPensjon.status)
        assertEquals("PARF", simulertPensjon.feilkode)
    }

    @Test
    @Throws(Exception::class)
    fun shouldReturnStatusAndFeilkodeWhenMissingStillingsprosent() {
        val tpOrdning = TPOrdning("1", "1")
        val tpLeverandor = TpLeverandor("lev1", "url1", SOAP)

        Mockito.`when`(tpRegisterConsumer.getTpOrdningerForPerson(anyNonNull())).thenReturn(listOf(tpOrdning))

        Mockito.`when`(stillingsprosentService.getStillingsprosentListe(anyNonNull(), anyNonNull())).thenReturn(StillingsprosentResponse(mapOf(tpOrdning to emptyList()), emptyList()))

        val asyncResponse = AsyncResponse<TPOrdning, TpLeverandor>().apply {
            resultMap[tpOrdning] = tpLeverandor
        }

        Mockito.`when`(asyncExecutor.executeAsync<TPOrdning>(anyNonNull())).thenReturn(asyncResponse)
        Mockito.`when`(stillingsprosentService.getLatestFromStillingsprosent(anyNonNull())).thenThrow(MissingStillingsprosentException("exception"))

        val simulertPensjon = simuleringService.simulerOffentligTjenestepensjon(request).simulertPensjonListe.first()

        assertNotNull(simulertPensjon)
        assertEquals("FEIL", simulertPensjon.status)
        assertEquals("IKKE", simulertPensjon.feilkode)
    }

    @Test
    fun `Should add response info when no stillingsprosent available`() {
        Mockito.`when`(asyncExecutor.executeAsync<TPOrdning>(anyNonNull())).thenReturn(AsyncResponse())
        Mockito.`when`(stillingsprosentService.getStillingsprosentListe(anyNonNull(), anyNonNull())).thenReturn(StillingsprosentResponse(emptyMap(), emptyList()))
        val response = simuleringService.simulerOffentligTjenestepensjon(request)
        assertEquals("FEIL", response.simulertPensjonListe.first().status)
    }

    @Test
    @Throws(Exception::class)
    fun shouldAddResponseInfoWhenNoTpOrdningFound() {
        Mockito.`when`(tpRegisterConsumer.getTpOrdningerForPerson(anyNonNull())).thenThrow(NoTpOrdningerFoundException("exception"))
        val response = simuleringService.simulerOffentligTjenestepensjon(request)
        assertEquals("FEIL", response.simulertPensjonListe.first().status)
    }

    @Test
    fun `Should add response info when simulering returns ok status`() {
        val map = mapOf(TPOrdning("tssInkluder", "tpInkluder") to emptyList<Stillingsprosent>())
        val exceptions = listOf(ExecutionException(StillingsprosentCallableException("msg", Throwable(), TPOrdning("tssUtelatt", "tpUtelatt"))))
        val stillingsprosentResponse = StillingsprosentResponse(map, exceptions)
        Mockito.`when`(stillingsprosentService.getStillingsprosentListe(anyNonNull(), anyNonNull())).thenReturn(stillingsprosentResponse)
        val s1 = SimulertPensjon(
                utbetalingsperioder = singletonList(Utbetalingsperiode(
                        grad = 0,
                        arligUtbetaling = 0.0,
                        datoTom = now(),
                        datoFom = now(),
                        ytelsekode = "avdod",
                        mangelfullSimuleringkode = "dodva"
                )),
                tpnr = "feil",
                navnOrdning = "feil"
        )
        val tpOrdning = TPOrdning("fake", "faker")
        val tpLeverandor = TpLeverandor("fake", "faker", SOAP)
        Mockito.`when`(asyncExecutor.executeAsync<TPOrdning>(anyNonNull())).thenReturn(AsyncResponse<TPOrdning, TpLeverandor>().apply { this.resultMap[tpOrdning] = tpLeverandor })
        Mockito.`when`(simuleringEnpointRouter.simulerPensjon(anyNonNull(), anyNonNull(), anyNonNull(), anyNonNull())).thenReturn(listOf(s1))
        Mockito.`when`(stillingsprosentService.getLatestFromStillingsprosent(anyNonNull())).thenReturn(tpOrdning)
        val response = simuleringService.simulerOffentligTjenestepensjon(request)
        Mockito.verify<AppMetrics>(metrics).incrementCounter(APP_NAME, APP_TOTAL_SIMULERING_UFUL)
        assertTrue("tpUtelatt" in response.simulertPensjonListe.first().utelatteTpnr)
        assertTrue("tpInkluder" in response.simulertPensjonListe.first().inkluderteTpnr)
        assertEquals("UFUL", response.simulertPensjonListe.first().status)
    }

    @Test
    fun `Should increment metrics`() {
        val map = mapOf(TPOrdning("tssInkluder", "tpInkluder") to emptyList<Stillingsprosent>())
        val stillingsprosentResponse = StillingsprosentResponse(map, listOf())
        Mockito.`when`(stillingsprosentService.getStillingsprosentListe(anyNonNull(), anyNonNull())).thenReturn(stillingsprosentResponse)

        val s1 = SimulertPensjon(
                utbetalingsperioder = singletonList(null),
                tpnr = "feil",
                navnOrdning = "feil"
        )
        val s2 = SimulertPensjon(
                utbetalingsperioder = singletonList(null),
                tpnr = "feil",
                navnOrdning = "feil"
        )
        val tpOrdning = TPOrdning("fake", "faker")
        val tpLeverandor = TpLeverandor("fake", "faker", SOAP)
        Mockito.`when`(asyncExecutor.executeAsync<TPOrdning>(anyNonNull())).thenReturn(AsyncResponse<TPOrdning, TpLeverandor>().apply { this.resultMap[tpOrdning] = tpLeverandor })
        Mockito.`when`(stillingsprosentService.getLatestFromStillingsprosent(anyNonNull())).thenReturn(tpOrdning)
        Mockito.`when`(simuleringEnpointRouter.simulerPensjon(anyNonNull(), anyNonNull(), anyNonNull(), anyNonNull())).thenReturn(listOf(s1, s2))
        val response = simuleringService.simulerOffentligTjenestepensjon(request)
        Mockito.verify<AppMetrics>(metrics).incrementCounter(APP_NAME, APP_TOTAL_SIMULERING_MANGEL)
        assertNull(response.simulertPensjonListe.first().status)
    }
}