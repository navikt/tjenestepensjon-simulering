package no.nav.tjenestepensjon.simulering.v2

import no.nav.tjenestepensjon.simulering.AppMetrics
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_NAME
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_SIMULERING_MANGEL
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_SIMULERING_UFUL
import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor.EndpointImpl.REST
import no.nav.tjenestepensjon.simulering.testHelper.anyNonNull
import no.nav.tjenestepensjon.simulering.v1.service.StillingsprosentResponse
import no.nav.tjenestepensjon.simulering.v2.exceptions.OpptjeningsperiodeCallableException
import no.nav.tjenestepensjon.simulering.v2.models.domain.Opptjeningsperiode
import no.nav.tjenestepensjon.simulering.v2.models.domain.SivilstandCodeEnum
import no.nav.tjenestepensjon.simulering.v2.models.domain.Utbetalingsperiode
import no.nav.tjenestepensjon.simulering.v2.models.request.SimulerPensjonRequest
import no.nav.tjenestepensjon.simulering.v2.models.response.SimulerOffentligTjenestepensjonResponse
import no.nav.tjenestepensjon.simulering.v2.rest.RestClient
import no.nav.tjenestepensjon.simulering.v2.service.OpptjeningsperiodeResponse
import no.nav.tjenestepensjon.simulering.v2.service.OpptjeningsperiodeService
import no.nav.tjenestepensjon.simulering.v2.service.SimpleSimuleringService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDate.now
import java.util.concurrent.ExecutionException
import kotlin.test.assertNotNull

@ExtendWith(MockitoExtension::class)
internal class SimpleSimuleringServiceTest {

    @Mock
    private lateinit var opptjeningsperiodeService: OpptjeningsperiodeService

    @Mock
    private lateinit var restClient: RestClient

    @Mock
    private lateinit var metrics: AppMetrics

    @InjectMocks
    private lateinit var simuleringService: SimpleSimuleringService

    private lateinit var request: SimulerPensjonRequest

    @BeforeEach
    fun beforeEach() {
        request = SimulerPensjonRequest(
                fnr = FNR("01011234567"),
                fodselsdato = "1968-01-01",
                sisteTpnr = "12345",
                sivilstandkode = SivilstandCodeEnum.GIFT,
                inntektListe = emptyList(),
                simuleringsperiodeListe = emptyList(),
                simuleringsdataListe = emptyList(),
                tpForholdListe = emptyList()
        )
    }

    @Test
    fun `Should return response object`() {
        val map = mapOf(TPOrdning("tssInkluder", "tpInkluder") to emptyList<Opptjeningsperiode>())
        val exceptions = listOf(ExecutionException(OpptjeningsperiodeCallableException("msg", Throwable(), TPOrdning("tssUtelatt", "tpUtelatt"))))
        val opptjeningsperiodeResponse = OpptjeningsperiodeResponse(map, exceptions)

        Mockito.`when`(opptjeningsperiodeService.getOpptjeningsperiodeListe(anyNonNull()))
                .thenReturn(opptjeningsperiodeResponse)

        val s1 = SimulerOffentligTjenestepensjonResponse(
                tpnr = "feil",
                navnOrdning = "feil",
                utbetalingsperiodeListe = listOf(Utbetalingsperiode(
                        uttaksgrad = 0,
                        arligUtbetaling = 0.0,
                        datoTom = now(),
                        datoFom = now(),
                        ytelsekode = "avdod"
                ))
        )
        val tpOrdning = TPOrdning("fake", "faker")
        val tpLeverandor = TpLeverandor("fake", REST, "faker", "faker")
        Mockito.`when`(restClient.getResponse(anyNonNull(), anyNonNull(), anyNonNull()))
                .thenReturn(s1)

        val response = simuleringService.simulerOffentligTjenestepensjon(
                request,
                StillingsprosentResponse(emptyMap(), emptyList()),
                tpOrdning,
                tpLeverandor
        )

        Mockito.verify<AppMetrics>(metrics).incrementCounter(APP_NAME, APP_TOTAL_SIMULERING_UFUL)

        assertNotNull(response)
    }

    @Test
    fun `Should increment metrics`() {
        val map = mapOf(TPOrdning("tssInkluder", "tpInkluder") to emptyList<Opptjeningsperiode>())
        val opptjeningsperiodeResponse = OpptjeningsperiodeResponse(map, listOf())

        Mockito.`when`(opptjeningsperiodeService.getOpptjeningsperiodeListe(anyNonNull()))
                .thenReturn(opptjeningsperiodeResponse)

        val s1 = SimulerOffentligTjenestepensjonResponse(
                utbetalingsperiodeListe = listOf(),
                tpnr = "",
                navnOrdning = ""
        )

        val tpOrdning = TPOrdning("fake", "faker")
        val tpLeverandor = TpLeverandor("fake", REST, "faker", "faker")

        Mockito.`when`(restClient.getResponse(anyNonNull(), anyNonNull(), anyNonNull()))
                .thenReturn(s1)
        assertNotNull(
                simuleringService.simulerOffentligTjenestepensjon(
                        request,
                        StillingsprosentResponse(emptyMap(), emptyList()),
                        tpOrdning,
                        tpLeverandor
                )
        )

        Mockito.verify<AppMetrics>(metrics).incrementCounter(APP_NAME, APP_TOTAL_SIMULERING_MANGEL)
    }
}