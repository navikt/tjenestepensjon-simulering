package no.nav.tjenestepensjon.simulering.v1

import no.nav.tjenestepensjon.simulering.AppMetrics
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.TP_TOTAL_SIMULERING_CALLS
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.TP_TOTAL_SIMULERING_TIME
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.TP_TOTAL_STILLINGSPROSENT_CALLS
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.TP_TOTAL_STILLINGSPROSENT_TIME
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor.EndpointImpl.REST
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor.EndpointImpl.SOAP
import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.v1.models.domain.Stillingsprosent
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.v1.models.request.SimulerPensjonRequest
import no.nav.tjenestepensjon.simulering.v1.models.response.SimulertPensjon
import no.nav.tjenestepensjon.simulering.v1.rest.RestClientOld
import no.nav.tjenestepensjon.simulering.v1.soap.SoapClient
import no.nav.tjenestepensjon.simulering.testHelper.anyNonNull
import no.nav.tjenestepensjon.simulering.testHelper.safeEq
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
internal class TjenestepensjonsimuleringEndpointRouterOldTest {

    private val fnr = FNR("01011234567")

    private val stillingsprosenter = listOf(
            Stillingsprosent(
                    stillingsprosent = 100.0,
                    aldersgrense = 70,
                    datoFom = LocalDate.of(2018, 1, 2),
                    datoTom = LocalDate.of(2029, 12, 31),
                    faktiskHovedlonn = "hovedlønn1",
                    stillingsuavhengigTilleggslonn = "tilleggslønn1"
            ),
            Stillingsprosent(
                    stillingsprosent = 12.5,
                    aldersgrense = 67,
                    datoFom = LocalDate.of(2019, 2, 3),
                    datoTom = LocalDate.of(2035, 11, 30),
                    faktiskHovedlonn = "hovedlønn2",
                    stillingsuavhengigTilleggslonn = "tilleggslønn2"
            )
    )
    private val simulerPensjonRequest = SimulerPensjonRequest(
            fnr = fnr,
            sivilstandkode = "",
            simuleringsperioder = emptyList(),
            pensjonsbeholdningsperioder = emptyList(),
            inntekter = emptyList()
    )

    @Mock
    private lateinit var restClient: RestClientOld
    @Mock
    private lateinit var soapClient: SoapClient
    @Mock
    private lateinit var metrics: AppMetrics
    @InjectMocks
    private lateinit var simuleringEndpointRouter: TjenestepensjonsimuleringEndpointRouterOld

    private val tpOrdning = TPOrdning("tss1", "tp1")
    private val tpRestLeverandor = TpLeverandor("lev", "url1", REST)
    private val tpSoapLeverandor = TpLeverandor("lev", "url1", SOAP)

    @Test
    fun `Call shall return stillingsprosenter with soap`() {
        Mockito.`when`(soapClient.getStillingsprosenter(anyNonNull(), anyNonNull(), anyNonNull())).thenReturn(stillingsprosenter)
        val result: List<Stillingsprosent> = simuleringEndpointRouter.getStillingsprosenter(fnr, tpOrdning, tpSoapLeverandor)
        Mockito.verify<AppMetrics>(metrics).incrementCounter(safeEq(tpSoapLeverandor.name), safeEq(TP_TOTAL_STILLINGSPROSENT_CALLS))
        Mockito.verify<AppMetrics>(metrics).incrementCounter(safeEq(tpSoapLeverandor.name), safeEq(TP_TOTAL_STILLINGSPROSENT_TIME), anyNonNull())
        assertStillingsprosenter(result)
    }

    @Test
    fun `Call shall return stillingsprosenter with rest`() {
        Mockito.`when`(restClient.getStillingsprosenter(anyNonNull(), anyNonNull(), anyNonNull())).thenReturn(stillingsprosenter)
        val result: List<Stillingsprosent> = simuleringEndpointRouter.getStillingsprosenter(fnr, tpOrdning, tpRestLeverandor)
        Mockito.verify<AppMetrics>(metrics).incrementCounter(safeEq(tpRestLeverandor.name), safeEq(TP_TOTAL_STILLINGSPROSENT_CALLS))
        Mockito.verify<AppMetrics>(metrics).incrementCounter(safeEq(tpRestLeverandor.name), safeEq(TP_TOTAL_STILLINGSPROSENT_TIME), anyNonNull())
        assertStillingsprosenter(result)
    }

    @Test
    fun `Call shall return simulerPensjon with soap`() {
        Mockito.`when`(soapClient.simulerPensjon(anyNonNull(), anyNonNull(), anyNonNull(), anyNonNull())).thenReturn(emptyList())
        val result: List<SimulertPensjon> = simuleringEndpointRouter.simulerPensjon(simulerPensjonRequest, tpOrdning, tpSoapLeverandor, emptyMap())
        Mockito.verify<AppMetrics>(metrics).incrementCounter(safeEq(tpSoapLeverandor.name), safeEq(TP_TOTAL_SIMULERING_CALLS))
        Mockito.verify<AppMetrics>(metrics).incrementCounter(safeEq(tpSoapLeverandor.name), safeEq(TP_TOTAL_SIMULERING_TIME), anyNonNull())
        assertEquals(result, emptyList<SimulertPensjon>())
    }

    @Test
    fun `Call shall return simulerPensjon with rest`() {
        Mockito.`when`(restClient.simulerPensjon(anyNonNull(), anyNonNull(), anyNonNull(), anyNonNull())).thenReturn(emptyList())
        val result: List<SimulertPensjon> = simuleringEndpointRouter.simulerPensjon(simulerPensjonRequest, tpOrdning, tpRestLeverandor, emptyMap())
        Mockito.verify<AppMetrics>(metrics).incrementCounter(safeEq(tpRestLeverandor.name), safeEq(TP_TOTAL_SIMULERING_CALLS))
        Mockito.verify<AppMetrics>(metrics).incrementCounter(safeEq(tpRestLeverandor.name), safeEq(TP_TOTAL_SIMULERING_TIME), anyNonNull())
        assertEquals(result, emptyList<SimulertPensjon>())
    }

    private fun assertStillingsprosenter(actual: List<Stillingsprosent>) {
        assertEquals(stillingsprosenter.size, actual.size)
        for (index in stillingsprosenter.indices) {
            assertEquals(stillingsprosenter[index], actual[index])
        }
    }
}