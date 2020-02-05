package no.nav.tjenestepensjon.simulering

import AsyncExecutor.AsyncResponse
import OutgoingResponse.SimulertPensjon
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.Stillingsprosent
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_NAME
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_SIMULERING_MANGEL
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_SIMULERING_UFUL
import no.nav.tjenestepensjon.simulering.AsyncExecutor.AsyncResponse
import no.nav.tjenestepensjon.simulering.consumer.TpRegisterConsumer
import no.nav.tjenestepensjon.simulering.model.v1.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor.EndpointImpl.SOAP
import no.nav.tjenestepensjon.simulering.exceptions.DuplicateStillingsprosentEndDateException
import no.nav.tjenestepensjon.simulering.exceptions.MissingStillingsprosentException
import no.nav.tjenestepensjon.simulering.exceptions.NoTpOrdningerFoundException
import no.nav.tjenestepensjon.simulering.exceptions.StillingsprosentCallableException
import no.nav.tjenestepensjon.simulering.model.v1.request.SimulerPensjonRequest
import no.nav.tjenestepensjon.simulering.model.v1.response.OutgoingResponse
import no.nav.tjenestepensjon.simulering.model.v1.response.OutgoingResponse.SimulertPensjon
import no.nav.tjenestepensjon.simulering.model.v1.response.OutgoingResponse.Utbetalingsperiode
import no.nav.tjenestepensjon.simulering.service.SimpleSimuleringService
import no.nav.tjenestepensjon.simulering.service.StillingsprosentResponse
import no.nav.tjenestepensjon.simulering.service.StillingsprosentService
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import java.util.concurrent.ExecutionException

@ExtendWith(MockitoExtension::class)
internal class SimpleSimuleringServiceTest {
    @Mock
    private val tpRegisterConsumer: TpRegisterConsumer? = null
    @Mock
    private val asyncExecutor: AsyncExecutor? = null
    @Mock
    private val stillingsprosentService: StillingsprosentService? = null
    @Mock
    private val simuleringEnpointRouter: TjenestepensjonsimuleringEndpointRouter? = null
    @Mock
    private val metrics: AppMetrics? = null
    @InjectMocks
    private val simuleringService: SimpleSimuleringService? = null
    private val request: SimulerPensjonRequest = SimulerPensjonRequest()
    @BeforeEach
    fun beforeEach() {
        request.setFnr("1234")
    }

    @Test
    @Throws(Exception::class)
    fun shouldReturnStatusAndFeilkodeWhenDuplicateStillingsprosentEndDate() {
        val tpOrdning = TPOrdning("1", "1")
        val tpLeverandor = TpLeverandor("lev1", "url1", SOAP)
        Mockito.`when`(tpRegisterConsumer.getTpOrdningerForPerson(ArgumentMatchers.any())).thenReturn(java.util.List.of(tpOrdning))
        val stillingsprosentResponse: StillingsprosentResponse = Mockito.mock(StillingsprosentResponse::class.java)
        Mockito.`when`(stillingsprosentResponse.getTpOrdningStillingsprosentMap()).thenReturn(java.util.Map.of(tpOrdning, java.util.List.of(Stillingsprosent())))
        Mockito.`when`(stillingsprosentService.getStillingsprosentListe(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(stillingsprosentResponse)
        val asyncResponse: AsyncResponse<TPOrdning, TpLeverandor> = AsyncResponse()
        asyncResponse.getResultMap().put(tpOrdning, tpLeverandor)
        Mockito.`when`(asyncExecutor.executeAsync(ArgumentMatchers.any())).thenReturn(asyncResponse)
        Mockito.`when`(stillingsprosentService.getLatestFromStillingsprosent(ArgumentMatchers.any(MutableMap::class.java))).thenThrow(DuplicateStillingsprosentEndDateException("exception"))
        val response: OutgoingResponse = simuleringService.simulerOffentligTjenestepensjon(request)
        val simulertPensjon: SimulertPensjon = response.getSimulertPensjonListe().get(0)
        MatcherAssert.assertThat(simulertPensjon, Matchers.`is`(Matchers.notNullValue()))
        assertThat(simulertPensjon.getStatus(), Matchers.`is`("FEIL"))
        assertThat(simulertPensjon.getFeilkode(), Matchers.`is`("PARF"))
    }

    @Test
    @Throws(Exception::class)
    fun shouldReturnStatusAndFeilkodeWhenMissingStillingsprosent() {
        val tpOrdning = TPOrdning("1", "1")
        val tpLeverandor = TpLeverandor("lev1", "url1", SOAP)
        Mockito.`when`(tpRegisterConsumer.getTpOrdningerForPerson(ArgumentMatchers.any())).thenReturn(java.util.List.of(tpOrdning))
        val stillingsprosentResponse: StillingsprosentResponse = Mockito.mock(StillingsprosentResponse::class.java)
        Mockito.`when`(stillingsprosentResponse.getTpOrdningStillingsprosentMap()).thenReturn(java.util.Map.of(tpOrdning, java.util.List.of(Stillingsprosent())))
        Mockito.`when`(stillingsprosentService.getStillingsprosentListe(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(stillingsprosentResponse)
        val asyncResponse: AsyncResponse<TPOrdning, TpLeverandor> = AsyncResponse()
        asyncResponse.getResultMap().put(tpOrdning, tpLeverandor)
        Mockito.`when`(asyncExecutor.executeAsync(ArgumentMatchers.any())).thenReturn(asyncResponse)
        Mockito.`when`(stillingsprosentService.getLatestFromStillingsprosent(ArgumentMatchers.any(MutableMap::class.java))).thenThrow(MissingStillingsprosentException("exception"))
        val response: OutgoingResponse = simuleringService.simulerOffentligTjenestepensjon(request)
        val simulertPensjon: SimulertPensjon = response.getSimulertPensjonListe().get(0)
        MatcherAssert.assertThat(simulertPensjon, Matchers.`is`(Matchers.notNullValue()))
        assertThat(simulertPensjon.getStatus(), Matchers.`is`("FEIL"))
        assertThat(simulertPensjon.getFeilkode(), Matchers.`is`("IKKE"))
    }

    @Test
    fun shouldAddResponseInfoWhenNoStillingsprosentAvailable() {
        Mockito.`when`(asyncExecutor.executeAsync(ArgumentMatchers.any())).thenReturn(AsyncResponse())
        Mockito.`when`(stillingsprosentService.getStillingsprosentListe(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Mockito.mock(StillingsprosentResponse::class.java))
        val response: OutgoingResponse = simuleringService.simulerOffentligTjenestepensjon(request)
        assertThat(response.getSimulertPensjonListe().get(0).getStatus(), Matchers.`is`("FEIL"))
    }

    @Test
    @Throws(Exception::class)
    fun shouldAddResponseInfoWhenNoTpOrdningFound() {
        Mockito.`when`(tpRegisterConsumer.getTpOrdningerForPerson(ArgumentMatchers.any())).thenThrow(NoTpOrdningerFoundException("exception"))
        val response: OutgoingResponse = simuleringService.simulerOffentligTjenestepensjon(request)
        assertThat(response.getSimulertPensjonListe().get(0).getStatus(), Matchers.`is`("FEIL"))
    }

    @Test
    fun shouldAddResponseInfoWhenSimuleringReturnsOkStatus() {
        Mockito.`when`(asyncExecutor.executeAsync(ArgumentMatchers.any())).thenReturn(AsyncResponse())
        val map: Map<TPOrdning, List<Stillingsprosent>> = java.util.Map.of<TPOrdning, List<Stillingsprosent>>(TPOrdning("tssInkluder", "tpInkluder"), java.util.List.of<Any>(Stillingsprosent()))
        val exceptions = java.util.List.of(ExecutionException(StillingsprosentCallableException("msg", null, TPOrdning("tssUtelatt", "tpUtelatt"))))
        val stillingsprosentResponse = StillingsprosentResponse(map, exceptions)
        Mockito.`when`(stillingsprosentService.getStillingsprosentListe(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(stillingsprosentResponse)
        val s1 = SimulertPensjon()
        val p1 = Utbetalingsperiode()
        s1.setUtbetalingsperioder(java.util.List.of(p1))
        Mockito.`when`(simuleringEnpointRouter.simulerPensjon(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(java.util.List.of(s1))
        val response: OutgoingResponse = simuleringService.simulerOffentligTjenestepensjon(request)
        Mockito.verify<Any?>(metrics).incrementCounter(APP_NAME, APP_TOTAL_SIMULERING_UFUL)
        assertThat(response.getSimulertPensjonListe().get(0).getUtelatteTpnr().contains("tpUtelatt"), Matchers.`is`(true))
        assertThat(response.getSimulertPensjonListe().get(0).getInkluderteTpnr().contains("tpInkluder"), Matchers.`is`(true))
        assertThat(response.getSimulertPensjonListe().get(0).getStatus(), Matchers.`is`("UFUL"))
    }

    @Test
    fun shouldIncrementMetrics() {
        Mockito.`when`(asyncExecutor.executeAsync(ArgumentMatchers.any())).thenReturn(AsyncResponse())
        val map: Map<TPOrdning, List<Stillingsprosent>> = java.util.Map.of<TPOrdning, List<Stillingsprosent>>(TPOrdning("tssInkluder", "tpInkluder"), java.util.List.of<Any>(Stillingsprosent()))
        val stillingsprosentResponse = StillingsprosentResponse(map, java.util.List.of())
        Mockito.`when`(stillingsprosentService.getStillingsprosentListe(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(stillingsprosentResponse)
        val s1 = SimulertPensjon()
        val p1 = Utbetalingsperiode()
        p1.setMangelfullSimuleringkode("PRIVAT")
        s1.setUtbetalingsperioder(java.util.List.of(p1))
        val s2 = SimulertPensjon()
        val p2 = Utbetalingsperiode()
        p2.setMangelfullSimuleringkode("LOPENDE")
        s2.setUtbetalingsperioder(java.util.List.of(p2))
        Mockito.`when`(simuleringEnpointRouter.simulerPensjon(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(java.util.List.of(s1, s2))
        val response: OutgoingResponse = simuleringService.simulerOffentligTjenestepensjon(request)
        Mockito.verify<Any?>(metrics).incrementCounter(APP_NAME, APP_TOTAL_SIMULERING_MANGEL)
        assertThat(response.getSimulertPensjonListe().get(0).getStatus(), Matchers.`is`(Matchers.nullValue()))
    }
}