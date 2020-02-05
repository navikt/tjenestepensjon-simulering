package no.nav.tjenestepensjon.simulering

import AsyncExecutor.AsyncResponse
import OutgoingResponse.SimulertPensjon
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.Stillingsprosent
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.TP_TOTAL_SIMULERING_CALLS
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.TP_TOTAL_SIMULERING_TIME
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.TP_TOTAL_STILLINGSPROSENT_CALLS
import no.nav.tjenestepensjon.simulering.AppMetrics.Metrics.TP_TOTAL_STILLINGSPROSENT_TIME
import no.nav.tjenestepensjon.simulering.model.v1.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor.EndpointImpl.REST
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor.EndpointImpl.SOAP
import no.nav.tjenestepensjon.simulering.model.v1.request.SimulerPensjonRequest
import no.nav.tjenestepensjon.simulering.rest.RestClient
import no.nav.tjenestepensjon.simulering.soap.SoapClient
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDate
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class TjenestepensjonsimuleringEndpointRouterTest {
    @Mock
    var simulerPensjonRequest: SimulerPensjonRequest? = null
    @Mock
    private val restClient: RestClient? = null
    @Mock
    private val soapClient: SoapClient? = null
    @Mock
    private val metrics: AppMetrics? = null
    @InjectMocks
    private val simuleringEndpointRouter: TjenestepensjonsimuleringEndpointRouter? = null

    @Test
    fun call_shall_return_stillingsprosenter_with_soap() {
        val tpOrdning: `var` = TPOrdning("tss1", "tp1")
        val tpLeverandor: `var` = TpLeverandor("lev", "url1", SOAP)
        val stillingsprosenter: List<Stillingsprosent> = prepareStillingsprosenter()
        Mockito.`when`(soapClient.getStillingsprosenter(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(stillingsprosenter)
        val result: List<Stillingsprosent> = simuleringEndpointRouter.getStillingsprosenter("fnr1", tpOrdning, tpLeverandor)
        Mockito.verify<Any?>(metrics).incrementCounter(eq(tpLeverandor.getName()), ArgumentMatchers.eq(TP_TOTAL_STILLINGSPROSENT_CALLS))
        Mockito.verify<Any?>(metrics).incrementCounter(eq(tpLeverandor.getName()), ArgumentMatchers.eq(TP_TOTAL_STILLINGSPROSENT_TIME), ArgumentMatchers.any(Double::class.javaPrimitiveType))
        assertStillingsprosenter(stillingsprosenter, result)
    }

    @Test
    fun call_shall_return_stillingsprosenter_with_rest() {
        val tpOrdning: `var` = TPOrdning("tss1", "tp1")
        val tpLeverandor: `var` = TpLeverandor("lev", "url1", REST)
        val stillingsprosenter: List<Stillingsprosent> = prepareStillingsprosenter()
        Mockito.`when`(restClient.getStillingsprosenter(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(stillingsprosenter)
        val result: List<Stillingsprosent> = simuleringEndpointRouter.getStillingsprosenter("fnr1", tpOrdning, tpLeverandor)
        Mockito.verify<Any?>(metrics).incrementCounter(eq(tpLeverandor.getName()), ArgumentMatchers.eq(TP_TOTAL_STILLINGSPROSENT_CALLS))
        Mockito.verify<Any?>(metrics).incrementCounter(eq(tpLeverandor.getName()), ArgumentMatchers.eq(TP_TOTAL_STILLINGSPROSENT_TIME), ArgumentMatchers.any(Double::class.javaPrimitiveType))
        assertStillingsprosenter(stillingsprosenter, result)
    }

    @Test
    fun call_shall_return_simulerPensjon_with_soap() {
        val tpOrdning: `var` = TPOrdning("tss1", "tp1")
        val tpLeverandor: `var` = TpLeverandor("lev", "url1", SOAP)
        Mockito.`when`(soapClient.simulerPensjon(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(ArrayList<E>())
        val result: List<SimulertPensjon> = simuleringEndpointRouter.simulerPensjon(simulerPensjonRequest, tpOrdning, tpLeverandor, Map.of())
        Mockito.verify<Any?>(metrics).incrementCounter(eq(tpLeverandor.getName()), ArgumentMatchers.eq(TP_TOTAL_SIMULERING_CALLS))
        Mockito.verify<Any?>(metrics).incrementCounter(eq(tpLeverandor.getName()), ArgumentMatchers.eq(TP_TOTAL_SIMULERING_TIME), ArgumentMatchers.any(Double::class.javaPrimitiveType))
        Assertions.assertEquals(result, ArrayList<SimulertPensjon>())
    }

    @Test
    fun call_shall_return_simulerPensjon_with_rest() {
        val tpOrdning: `var` = TPOrdning("tss1", "tp1")
        val tpLeverandor: `var` = TpLeverandor("lev", "url1", REST)
        Mockito.`when`(restClient.simulerPensjon(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(ArrayList<E>())
        val result: List<SimulertPensjon> = simuleringEndpointRouter.simulerPensjon(simulerPensjonRequest, tpOrdning, tpLeverandor, Map.of())
        Mockito.verify<Any?>(metrics).incrementCounter(eq(tpLeverandor.getName()), ArgumentMatchers.eq(TP_TOTAL_SIMULERING_CALLS))
        Mockito.verify<Any?>(metrics).incrementCounter(eq(tpLeverandor.getName()), ArgumentMatchers.eq(TP_TOTAL_SIMULERING_TIME), ArgumentMatchers.any(Double::class.javaPrimitiveType))
        Assertions.assertEquals(result, ArrayList<SimulertPensjon>())
    }

    private class StillingsprosentBuilder {
        private val stillingsprosent: Stillingsprosent = Stillingsprosent()
        fun stillingsprosent(value: Double): StillingsprosentBuilder {
            stillingsprosent.setStillingsprosent(value)
            return this
        }

        fun datoFom(year: Int, month: Int, day: Int): StillingsprosentBuilder {
            stillingsprosent.setDatoFom(LocalDate.of(year, month, day))
            return this
        }

        fun datoTom(year: Int, month: Int, day: Int): StillingsprosentBuilder {
            stillingsprosent.setDatoTom(LocalDate.of(year, month, day))
            return this
        }

        fun faktiskHovedlonn(value: String?): StillingsprosentBuilder {
            stillingsprosent.setFaktiskHovedlonn(value)
            return this
        }

        fun stillingsuavhengigTilleggslonn(value: String?): StillingsprosentBuilder {
            stillingsprosent.setStillingsuavhengigTilleggslonn(value)
            return this
        }

        fun aldersgrense(value: Int): StillingsprosentBuilder {
            stillingsprosent.setAldersgrense(value)
            return this
        }

        fun build(): Stillingsprosent {
            return stillingsprosent
        }
    }

    companion object {
        private fun prepareStillingsprosenter(): List<Stillingsprosent> {
            val stillingsprosent1: `var` = StillingsprosentBuilder()
                    .stillingsprosent(100.0)
                    .aldersgrense(70)
                    .datoFom(2018, 1, 2)
                    .datoTom(2029, 12, 31)
                    .faktiskHovedlonn("hovedlønn1")
                    .stillingsuavhengigTilleggslonn("tilleggslønn1")
                    .build()
            val stillingsprosent2: `var` = StillingsprosentBuilder()
                    .stillingsprosent(12.5)
                    .aldersgrense(67)
                    .datoFom(2019, 2, 3)
                    .datoTom(2035, 11, 30)
                    .faktiskHovedlonn("hovedlønn2")
                    .stillingsuavhengigTilleggslonn("tilleggslønn2")
                    .build()
            return Arrays.asList<Stillingsprosent>(stillingsprosent1, stillingsprosent2)
        }

        private fun assertStillingsprosenter(expected: List<Stillingsprosent>, actual: List<Stillingsprosent>) {
            Assertions.assertEquals(expected.size, actual.size)
            for (index in expected.indices) {
                assertStillingsprosent(expected[index], actual[index])
            }
        }

        private fun assertStillingsprosent(expected: Stillingsprosent, actual: Stillingsprosent) {
            assertEquals(expected.getStillingsprosent(), actual.getStillingsprosent())
            assertEquals(expected.getDatoFom(), actual.getDatoFom())
            assertEquals(expected.getDatoTom(), actual.getDatoTom())
            assertEquals(expected.getFaktiskHovedlonn(), actual.getFaktiskHovedlonn())
            assertEquals(expected.getStillingsuavhengigTilleggslonn(), actual.getStillingsuavhengigTilleggslonn())
            assertEquals(expected.getAldersgrense(), actual.getAldersgrense())
        }
    }
}