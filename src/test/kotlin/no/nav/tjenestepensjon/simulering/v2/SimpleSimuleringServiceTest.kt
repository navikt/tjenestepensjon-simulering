package no.nav.tjenestepensjon.simulering.v2

import no.nav.tjenestepensjon.simulering.AppMetrics
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_NAME
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_SIMULERING_MANGEL
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_SIMULERING_UFUL
import no.nav.tjenestepensjon.simulering.AsyncExecutor
import no.nav.tjenestepensjon.simulering.AsyncExecutor.AsyncResponse
import no.nav.tjenestepensjon.simulering.consumer.TpConfigConsumer
import no.nav.tjenestepensjon.simulering.consumer.TpRegisterConsumer
import no.nav.tjenestepensjon.simulering.exceptions.NoTpOrdningerFoundException
import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.testHelper.anyNonNull
import no.nav.tjenestepensjon.simulering.v2.consumer.FindTpLeverandorCallable
import no.nav.tjenestepensjon.simulering.v2.exceptions.DuplicateOpptjeningsperiodeEndDateException
import no.nav.tjenestepensjon.simulering.v2.exceptions.MissingOpptjeningsperiodeException
import no.nav.tjenestepensjon.simulering.v2.exceptions.NoTpOpptjeningsPeriodeFoundException
import no.nav.tjenestepensjon.simulering.v2.exceptions.OpptjeningsperiodeCallableException
import no.nav.tjenestepensjon.simulering.v2.models.domain.Opptjeningsperiode
import no.nav.tjenestepensjon.simulering.v2.models.domain.SivilstandCodeEnum
import no.nav.tjenestepensjon.simulering.v2.models.domain.Utbetalingsperiode
import no.nav.tjenestepensjon.simulering.v2.models.request.SimulerPensjonRequest
import no.nav.tjenestepensjon.simulering.v2.models.response.SimulerOffentligTjenestepensjonResponse
import no.nav.tjenestepensjon.simulering.v2.service.OpptjeningsperiodeResponse
import no.nav.tjenestepensjon.simulering.v2.service.OpptjeningsperiodeService
import no.nav.tjenestepensjon.simulering.v2.service.SimpleSimuleringService
import org.junit.Ignore
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDate.now
import java.util.concurrent.ExecutionException

@ExtendWith(MockitoExtension::class)
internal class SimpleSimuleringServiceTest {

//    @Mock
//    private lateinit var tpRegisterConsumer: TpRegisterConsumer
//
//    @Mock
//    private lateinit var asyncExecutor: AsyncExecutor<TpLeverandor, FindTpLeverandorCallable>
//
//    @Mock
//    private lateinit var opptjeningsperiodeService: OpptjeningsperiodeService
//
//    @Mock
//    private lateinit var simuleringEnpointRouter: TjenestepensjonsimuleringEndpointRouter
//
//    @Mock
//    private lateinit var metrics: AppMetrics
//
//    @Mock
//    private lateinit var tpConfigConsumer: TpConfigConsumer
//
//    @Mock
//    private lateinit var tpLeverandorList: List<TpLeverandor>
//
//    @InjectMocks
//    private lateinit var simuleringService: SimpleSimuleringService
//
//    private lateinit var request: SimulerPensjonRequest
//
//    @BeforeEach
//    fun beforeEach() {
//        request = SimulerPensjonRequest(
//                fnr = FNR("01011234567"),
//                fodselsdato = "1968-01-01",
//                sisteTpnr = "12345",
//                sivilstandkode = SivilstandCodeEnum.GIFT,
//                inntektListe = emptyList(),
//                simuleringsperiodeListe = emptyList(),
//                simuleringsdataListe = emptyList(),
//                tpForholdListe = emptyList()
//        )
//    }
//
//    @Test
//    @Ignore
//    @Throws(DuplicateOpptjeningsperiodeEndDateException::class)
//    fun shouldReturnStatusAndFeilkodeWhenDuplicateOpptjeningsperiodeEndDate() {
//        val tpOrdning = TPOrdning("1", "1")
//        val tpLeverandor = TpLeverandor("lev2", "url1", null)
//
//        Mockito.`when`(tpRegisterConsumer.getTpOrdningerForPerson(anyNonNull())).thenReturn(listOf(tpOrdning))
//
//        Mockito.`when`(opptjeningsperiodeService.getOpptjeningsperiodeListe(anyNonNull(), anyNonNull())).thenReturn(
//                OpptjeningsperiodeResponse(
//                        mapOf(tpOrdning to emptyList()),
//                        emptyList()
//                )
//        )
//
//        val asyncResponse = AsyncResponse<TPOrdning, TpLeverandor>().apply {
//            resultMap[tpOrdning] = tpLeverandor
//        }
//
//        Mockito.`when`(asyncExecutor.executeAsync<TPOrdning>(anyNonNull())).thenReturn(asyncResponse)
//        Mockito.`when`(opptjeningsperiodeService.getLatestFromOpptjeningsperiode(anyNonNull()))
//                .thenThrow(DuplicateOpptjeningsperiodeEndDateException("exception")) //todo set versioning
//
//        val exception = assertThrows<DuplicateOpptjeningsperiodeEndDateException> { simuleringService.simulerOffentligTjenestepensjon(request) }
//
//        assertEquals(exception::class, DuplicateOpptjeningsperiodeEndDateException::class)
//        assertEquals("PARF", exception.feilkode)
//    }
//
//    @Test
//    @Ignore
//    @Throws(MissingOpptjeningsperiodeException::class)
//    fun shouldReturnStatusAndFeilkodeWhenMissingStillingsprosent() {
//        val tpOrdning = TPOrdning("1", "1")
//        val tpLeverandor = TpLeverandor("lev2", "url1", null)
//
//        Mockito.`when`(tpRegisterConsumer.getTpOrdningerForPerson(anyNonNull())).thenReturn(listOf(tpOrdning))
//
//        Mockito.`when`(opptjeningsperiodeService.getOpptjeningsperiodeListe(anyNonNull(), anyNonNull()))
//                .thenReturn(OpptjeningsperiodeResponse(mapOf(tpOrdning to emptyList()), emptyList()))
//
//        val asyncResponse = AsyncResponse<TPOrdning, TpLeverandor>().apply {
//            resultMap[tpOrdning] = tpLeverandor
//        }
//
//        Mockito.`when`(asyncExecutor.executeAsync<TPOrdning>(anyNonNull())).thenReturn(asyncResponse)
//        Mockito.`when`(opptjeningsperiodeService.getLatestFromOpptjeningsperiode(anyNonNull()))
//                .thenThrow(MissingOpptjeningsperiodeException("exception"))
//
//        val exception = assertThrows<MissingOpptjeningsperiodeException> { simuleringService.simulerOffentligTjenestepensjon(request) }
//
//        assertEquals(exception::class, MissingOpptjeningsperiodeException::class)
//        assertEquals("IKKE", exception.feilkode)
//    }
//
//    @Test
//    @Ignore
//    @Throws(NoTpOrdningerFoundException::class)
//    fun `Should add response info when no opptjeningsperiode available`() {
//        Mockito.`when`(asyncExecutor.executeAsync<TPOrdning>(anyNonNull())).thenReturn(AsyncResponse())
//        Mockito.`when`(opptjeningsperiodeService.getOpptjeningsperiodeListe(anyNonNull(), anyNonNull()))
//                .thenReturn(OpptjeningsperiodeResponse(emptyMap(), emptyList()))
//
//        val exception = assertThrows<NoTpOpptjeningsPeriodeFoundException> { simuleringService.simulerOffentligTjenestepensjon(request) }
//
//        assertEquals(exception::class, NoTpOpptjeningsPeriodeFoundException::class)
//    }
//
//    @Test
//    @Ignore
//    @Throws(NoTpOrdningerFoundException::class)
//    fun `should add response info when no tp ordning found`() {
//        Mockito.`when`(tpRegisterConsumer.getTpOrdningerForPerson(anyNonNull())).thenThrow(NoTpOrdningerFoundException("exception"))
//
//        val exception = assertThrows<NoTpOrdningerFoundException> { simuleringService.simulerOffentligTjenestepensjon(request) }
//
//        assertEquals(exception::class, NoTpOrdningerFoundException::class)
//    }
//
//    @Test
//    @Ignore
//    fun `Should return response object`() {
//        val map = mapOf(TPOrdning("tssInkluder", "tpInkluder") to emptyList<Opptjeningsperiode>())
//        val exceptions = listOf(ExecutionException(OpptjeningsperiodeCallableException("msg", Throwable(), TPOrdning("tssUtelatt", "tpUtelatt"))))
//        val opptjeningsperiodeResponse = OpptjeningsperiodeResponse(map, exceptions)
//
//        Mockito.`when`(opptjeningsperiodeService.getOpptjeningsperiodeListe(anyNonNull(), anyNonNull()))
//                .thenReturn(opptjeningsperiodeResponse)
//
//        val s1 = SimulerOffentligTjenestepensjonResponse(
//                tpnr = "feil",
//                navnOrdning = "feil",
//                utbetalingsperiodeListe = listOf(Utbetalingsperiode(
//                        grad = 0,
//                        arligUtbetaling = 0.0,
//                        datoTom = now(),
//                        datoFom = now(),
//                        ytelsekode = "avdod"
//                ))
//        )
//        val tpOrdning = TPOrdning("fake", "faker")
//        val tpLeverandor = TpLeverandor("fake", "faker", null)
//        Mockito.`when`(asyncExecutor.executeAsync<TPOrdning>(anyNonNull()))
//                .thenReturn(AsyncResponse<TPOrdning, TpLeverandor>().apply { this.resultMap[tpOrdning] = tpLeverandor })
//        Mockito.`when`(simuleringEnpointRouter.simulerPensjon(anyNonNull(), anyNonNull(), anyNonNull(), anyNonNull()))
//                .thenReturn(s1)
//        Mockito.`when`(opptjeningsperiodeService.getLatestFromOpptjeningsperiode(anyNonNull()))
//                .thenReturn(tpOrdning)
//
//        val response = simuleringService.simulerOffentligTjenestepensjon(request)
//
//        Mockito.verify<AppMetrics>(metrics).incrementCounter(APP_NAME, APP_TOTAL_SIMULERING_UFUL)
//
//        assertNotNull(response)
//    }
//
//    @Test
//    @Ignore
//    fun `Should increment metrics`() {
//        val map = mapOf(TPOrdning("tssInkluder", "tpInkluder") to emptyList<Opptjeningsperiode>())
//        val opptjeningsperiodeResponse = OpptjeningsperiodeResponse(map, listOf())
//
//        Mockito.`when`(opptjeningsperiodeService.getOpptjeningsperiodeListe(anyNonNull(), anyNonNull()))
//                .thenReturn(opptjeningsperiodeResponse)
//
//        val s1 = SimulerOffentligTjenestepensjonResponse(
//                utbetalingsperiodeListe = listOf(),
//                tpnr = "",
//                navnOrdning = ""
//        )
//
//        val tpOrdning = TPOrdning("fake", "faker")
//        val tpLeverandor = TpLeverandor("fake", "faker", null)
//
//        Mockito.`when`(asyncExecutor.executeAsync<TPOrdning>(anyNonNull())).thenReturn(AsyncResponse<TPOrdning, TpLeverandor>().apply { this.resultMap[tpOrdning] = tpLeverandor })
//        Mockito.`when`(opptjeningsperiodeService.getLatestFromOpptjeningsperiode(anyNonNull()))
//                .thenReturn(tpOrdning)
//        Mockito.`when`(simuleringEnpointRouter.simulerPensjon(anyNonNull(), anyNonNull(), anyNonNull(), anyNonNull()))
//                .thenReturn(s1)
//        assertNotNull(
//                simuleringService.simulerOffentligTjenestepensjon(request)
//        )
//
//        Mockito.verify<AppMetrics>(metrics).incrementCounter(APP_NAME, APP_TOTAL_SIMULERING_MANGEL)
//    }
}