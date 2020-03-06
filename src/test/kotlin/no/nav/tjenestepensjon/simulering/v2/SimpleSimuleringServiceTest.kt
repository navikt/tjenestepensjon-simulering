package no.nav.tjenestepensjon.simulering.v2

import no.nav.tjenestepensjon.simulering.AppMetrics
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_NAME
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_SIMULERING_MANGEL
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_SIMULERING_UFUL
import no.nav.tjenestepensjon.simulering.AsyncExecutor
import no.nav.tjenestepensjon.simulering.AsyncExecutor.AsyncResponse
import no.nav.tjenestepensjon.simulering.v2.consumer.FindTpLeverandorCallable
import no.nav.tjenestepensjon.simulering.consumer.TpConfigConsumer
import no.nav.tjenestepensjon.simulering.consumer.TpRegisterConsumer
import no.nav.tjenestepensjon.simulering.v2.models.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.exceptions.DuplicateStillingsprosentEndDateException
import no.nav.tjenestepensjon.simulering.v2.exceptions.MissingOpptjeningsperiodeException
import no.nav.tjenestepensjon.simulering.exceptions.NoTpOrdningerFoundException
import no.nav.tjenestepensjon.simulering.exceptions.StillingsprosentCallableException
import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.v2.models.domain.Utbetalingsperiode
import no.nav.tjenestepensjon.simulering.v2.models.request.SimulerPensjonRequest
import no.nav.tjenestepensjon.simulering.testHelper.anyNonNull
import no.nav.tjenestepensjon.simulering.v2.models.domain.Opptjeningsperiode
import no.nav.tjenestepensjon.simulering.v2.models.domain.SivilstandCodeEnum
import no.nav.tjenestepensjon.simulering.v2.models.response.SimulerOffentligTjenestepensjonResponse
import no.nav.tjenestepensjon.simulering.v2.service.OpptjeningsperiodeResponse
import no.nav.tjenestepensjon.simulering.v2.service.OpptjeningsperiodeService
import no.nav.tjenestepensjon.simulering.v2.service.SimpleSimuleringService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDate.now

import java.util.concurrent.ExecutionException

@ExtendWith(MockitoExtension::class)
internal class SimpleSimuleringServiceTest {

    @Mock
    private lateinit var tpRegisterConsumer: TpRegisterConsumer

    @Mock
    private lateinit var asyncExecutor: AsyncExecutor<TpLeverandor, FindTpLeverandorCallable>

    @Mock
    private lateinit var opptjeningsperiodeService: OpptjeningsperiodeService

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
                fodselsdato = "1968-01-01",
                sisteTpnr = "12345",
                sivilstandKode = SivilstandCodeEnum.GIFT,
                inntektListe = emptyList(),
                simuleringsperiodeListe = emptyList(),
                simuleringsdataListe = emptyList(),
                tpForholdListe = emptyList()
        )
    }

    @Test
    @Throws(Exception::class)
    fun shouldReturnStatusAndFeilkodeWhenDuplicateOpptjeningsperiodeEndDate() {
        val tpOrdning = TPOrdning("1", "1")
        val tpLeverandor = TpLeverandor("lev2", "url1")

        Mockito.`when`(tpRegisterConsumer.getTpOrdningerForPerson(anyNonNull())).thenReturn(listOf(tpOrdning))

        Mockito.`when`(opptjeningsperiodeService.getOpptjeningsperiodeListe(anyNonNull(), anyNonNull())).thenReturn(
                OpptjeningsperiodeResponse(
                        mapOf(tpOrdning to emptyList()),
                        emptyList()
                )
        )

        val asyncResponse = AsyncResponse<TPOrdning, TpLeverandor>().apply {
            resultMap[tpOrdning] = tpLeverandor
        }

        Mockito.`when`(asyncExecutor.executeAsync<TPOrdning>(anyNonNull())).thenReturn(asyncResponse)
        Mockito.`when`(opptjeningsperiodeService.getLatestFromOpptjeningsperiode(anyNonNull()))
                .thenThrow(DuplicateStillingsprosentEndDateException("exception")) //todo set versioning

        val simulertPensjon = simuleringService.simulerOffentligTjenestepensjon(request)

        assertNotNull(simulertPensjon) //todo fix response
    }

    @Test
    @Throws(Exception::class)
    fun shouldReturnStatusAndFeilkodeWhenMissingStillingsprosent() {
        val tpOrdning = TPOrdning("1", "1")
        val tpLeverandor = TpLeverandor("lev2", "url1")

        Mockito.`when`(tpRegisterConsumer.getTpOrdningerForPerson(anyNonNull())).thenReturn(listOf(tpOrdning))

        Mockito.`when`(opptjeningsperiodeService.getOpptjeningsperiodeListe(anyNonNull(), anyNonNull()))
                .thenReturn(OpptjeningsperiodeResponse(mapOf(tpOrdning to emptyList()), emptyList()))

        val asyncResponse = AsyncResponse<TPOrdning, TpLeverandor>().apply {
            resultMap[tpOrdning] = tpLeverandor
        }

        Mockito.`when`(asyncExecutor.executeAsync<TPOrdning>(anyNonNull())).thenReturn(asyncResponse)
        Mockito.`when`(opptjeningsperiodeService.getLatestFromOpptjeningsperiode(anyNonNull()))
                .thenThrow(MissingOpptjeningsperiodeException("exception"))

        val simulertPensjon = simuleringService.simulerOffentligTjenestepensjon(request)

        assertNotNull(simulertPensjon) //todo fix response
    }

    @Test
    fun `Should add response info when no stillingsprosent available`() {
        Mockito.`when`(asyncExecutor.executeAsync<TPOrdning>(anyNonNull())).thenReturn(AsyncResponse())
        Mockito.`when`(opptjeningsperiodeService.getOpptjeningsperiodeListe(anyNonNull(), anyNonNull()))
                .thenReturn(OpptjeningsperiodeResponse(emptyMap(), emptyList()))
        val response = simuleringService.simulerOffentligTjenestepensjon(request)

        assertEquals("FEIL", response.leverandorUrl) //todo fix response
    }

    @Test
    @Throws(Exception::class)
    fun shouldAddResponseInfoWhenNoTpOrdningFound() {
        Mockito.`when`(tpRegisterConsumer.getTpOrdningerForPerson(anyNonNull())).thenThrow(NoTpOrdningerFoundException("exception"))
        val response = simuleringService.simulerOffentligTjenestepensjon(request)

        assertEquals("FEIL", response.leverandorUrl) //todo fix response
    }

    @Test
    fun `Should add response info when simulering returns ok status`() {
        val map = mapOf(TPOrdning("tssInkluder", "tpInkluder") to emptyList<Opptjeningsperiode>())
        val exceptions = listOf(ExecutionException(StillingsprosentCallableException("msg", Throwable(), TPOrdning("tssUtelatt", "tpUtelatt"))))
        val opptjeningsperiodeResponse = OpptjeningsperiodeResponse(map, exceptions)
        Mockito.`when`(opptjeningsperiodeService.getOpptjeningsperiodeListe(anyNonNull(), anyNonNull())).thenReturn(opptjeningsperiodeResponse)
        val s1 = SimulerOffentligTjenestepensjonResponse(
                tpnr = "feil",
                navnOrdning = "feil",
                utbetalingsperiodeListe = listOf(Utbetalingsperiode(
                        grad = 0,
                        arligUtbetaling = 0.0,
                        datoTom = now(),
                        datoFom = now(),
                        ytelsekode = "avdod"
                ))
        )
        val tpOrdning = TPOrdning("fake", "faker")
        val tpLeverandor = TpLeverandor("fake", "faker")
        Mockito.`when`(asyncExecutor.executeAsync<TPOrdning>(anyNonNull())).thenReturn(AsyncResponse<TPOrdning, TpLeverandor>().apply { this.resultMap[tpOrdning] = tpLeverandor })
        Mockito.`when`(simuleringEnpointRouter.simulerPensjon(anyNonNull(), anyNonNull(), anyNonNull(), anyNonNull()))
                .thenReturn(s1)
        Mockito.`when`(opptjeningsperiodeService.getLatestFromOpptjeningsperiode(anyNonNull()))
                .thenReturn(tpOrdning)

        val response = simuleringService.simulerOffentligTjenestepensjon(request)
        Mockito.verify<AppMetrics>(metrics).incrementCounter(APP_NAME, APP_TOTAL_SIMULERING_UFUL)
        val simulertPensjon = response
        assertNotNull(simulertPensjon)
        assertEquals("UFUL", simulertPensjon.leverandorUrl) //todo fix response
    }

    @Test
    fun `Should increment metrics`() {
        val map = mapOf(TPOrdning("tssInkluder", "tpInkluder") to emptyList<Opptjeningsperiode>())
        val opptjeningsperiodeResponse = OpptjeningsperiodeResponse(map, listOf())
        Mockito.`when`(opptjeningsperiodeService.getOpptjeningsperiodeListe(anyNonNull(), anyNonNull())).thenReturn(opptjeningsperiodeResponse)

        val s1 = SimulerOffentligTjenestepensjonResponse(
                utbetalingsperiodeListe = listOf(null),
                tpnr = "",
                navnOrdning = ""
        )

        val tpOrdning = TPOrdning("fake", "faker")
        val tpLeverandor = TpLeverandor("fake", "faker")
        Mockito.`when`(asyncExecutor.executeAsync<TPOrdning>(anyNonNull())).thenReturn(AsyncResponse<TPOrdning, TpLeverandor>().apply { this.resultMap[tpOrdning] = tpLeverandor })
        Mockito.`when`(opptjeningsperiodeService.getLatestFromOpptjeningsperiode(anyNonNull()))
                .thenReturn(tpOrdning)
        Mockito.`when`(simuleringEnpointRouter.simulerPensjon(anyNonNull(), anyNonNull(), anyNonNull(), anyNonNull()))
                .thenReturn(s1)

        val response = simuleringService.simulerOffentligTjenestepensjon(request)
        Mockito.verify<AppMetrics>(metrics).incrementCounter(APP_NAME, APP_TOTAL_SIMULERING_MANGEL)

        assertNull(response.leverandorUrl)  //todo fix response
    }
}