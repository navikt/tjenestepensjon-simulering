package no.nav.tjenestepensjon.simulering.v1

import no.nav.tjenestepensjon.simulering.AppMetrics
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_NAME
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_SIMULERING_MANGEL
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.APP_TOTAL_SIMULERING_UFUL
import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdningIdDto
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor.EndpointImpl.SOAP
import no.nav.tjenestepensjon.simulering.testHelper.anyNonNull
import no.nav.tjenestepensjon.simulering.v1.exceptions.StillingsprosentCallableException
import no.nav.tjenestepensjon.simulering.v1.models.domain.Stillingsprosent
import no.nav.tjenestepensjon.simulering.v1.models.domain.Utbetalingsperiode
import no.nav.tjenestepensjon.simulering.v1.models.request.SimulerPensjonRequestV1
import no.nav.tjenestepensjon.simulering.v1.models.response.SimulertPensjon
import no.nav.tjenestepensjon.simulering.v1.service.SimuleringServiceV1
import no.nav.tjenestepensjon.simulering.v1.service.StillingsprosentResponse
import no.nav.tjenestepensjon.simulering.v1.soap.SoapClient
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDate.now
import java.util.concurrent.ExecutionException

@ExtendWith(MockitoExtension::class)
internal class SimpleSimuleringServiceTest {

    @Mock
    private lateinit var soapClient: SoapClient

    @Mock
    private lateinit var metrics: AppMetrics

    @InjectMocks
    private lateinit var simuleringService: SimuleringServiceV1

    private lateinit var request: SimulerPensjonRequestV1

    @BeforeEach
    fun beforeEach() {
        request = SimulerPensjonRequestV1(
            fnr = FNR("01011234567"),
            sivilstandkode = "ugift",
            simuleringsperioder = emptyList(),
            inntekter = emptyList()
        )
    }

    @Test
    fun `Should add response info when simulering returns ok status`() {
        val map = mapOf(TPOrdningIdDto("tssInkluder", "tpInkluder") to emptyList<Stillingsprosent>())
        val exceptions = listOf(
            ExecutionException(
                StillingsprosentCallableException(
                    "msg", Throwable(), TPOrdningIdDto("tssUtelatt", "tpUtelatt")
                )
            )
        )
        val stillingsprosentResponse = StillingsprosentResponse(map, exceptions)
        val s1 = SimulertPensjon(
            utbetalingsperioder = listOf(
                Utbetalingsperiode(
                    grad = 0,
                    arligUtbetaling = 0.0,
                    datoTom = now(),
                    datoFom = now(),
                    ytelsekode = "avdod",
                    mangelfullSimuleringkode = "dodva"
                )
            ), tpnr = "feil", navnOrdning = "feil"
        )
        val tpOrdningIdDto = TPOrdningIdDto("fake", "faker")
        val tpLeverandor = TpLeverandor("fake", SOAP, "faker", "faker")
        `when`(soapClient.simulerPensjon(anyNonNull(), anyNonNull(), anyNonNull(), anyNonNull())).thenReturn(listOf(s1))
        val response = simuleringService.simulerOffentligTjenestepensjon(
            request, stillingsprosentResponse, tpOrdningIdDto, tpLeverandor
        )
        verify(metrics).incrementCounter(APP_NAME, APP_TOTAL_SIMULERING_UFUL)
        val simulertPensjon = response.simulertPensjonListe.first()
        assertNotNull(simulertPensjon)
        assertTrue("tpUtelatt" in (simulertPensjon.utelatteTpnr ?: emptyList()))
        assertTrue("tpInkluder" in (simulertPensjon.inkluderteTpnr ?: emptyList()))
        assertEquals("UFUL", simulertPensjon.status)
    }

    @Test
    fun `Should increment metrics`() {
        val map = mapOf(TPOrdningIdDto("tssInkluder", "tpInkluder") to emptyList<Stillingsprosent>())
        val stillingsprosentResponse = StillingsprosentResponse(map, listOf())

        val s1 = SimulertPensjon(
            utbetalingsperioder = listOf(null), tpnr = "feil", navnOrdning = "feil"
        )
        val s2 = SimulertPensjon(
            utbetalingsperioder = listOf(null), tpnr = "feil", navnOrdning = "feil"
        )
        val tpOrdningIdDto = TPOrdningIdDto("fake", "faker")
        val tpLeverandor = TpLeverandor("fake", SOAP, "faker", "faker")
        `when`(soapClient.simulerPensjon(anyNonNull(), anyNonNull(), anyNonNull(), anyNonNull())).thenReturn(
            listOf(s1, s2)
        )
        val response = simuleringService.simulerOffentligTjenestepensjon(
            request, stillingsprosentResponse, tpOrdningIdDto, tpLeverandor
        )
        verify(metrics).incrementCounter(APP_NAME, APP_TOTAL_SIMULERING_MANGEL)
        assertNull(response.simulertPensjonListe.first().status)
    }
}
