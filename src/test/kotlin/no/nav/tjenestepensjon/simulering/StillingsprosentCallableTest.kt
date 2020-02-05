package no.nav.tjenestepensjon.simulering

import AsyncExecutor.AsyncResponse
import OutgoingResponse.SimulertPensjon
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.Stillingsprosent
import no.nav.tjenestepensjon.simulering.model.v1.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor.EndpointImpl.REST
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor.EndpointImpl.SOAP
import no.nav.tjenestepensjon.simulering.exceptions.StillingsprosentCallableException
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.function.Executable
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.ws.client.WebServiceIOException
import java.time.LocalDate
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class StillingsprosentCallableTest {
    @Mock
    private val simuleringEndPointRouter: TjenestepensjonsimuleringEndpointRouter? = null

    @Test
    @Throws(Exception::class)
    fun call_shall_return_stillingsprosenter_with_soap() {
        val tpOrdning: `var` = TPOrdning("tss1", "tp1")
        val tpLeverandor: `var` = TpLeverandor("lev", "url1", SOAP)
        val callable: `var` = StillingsprosentCallable("fnr1", tpOrdning, tpLeverandor, simuleringEndPointRouter)
        val stillingsprosenter: List<Stillingsprosent> = prepareStillingsprosenter()
        Mockito.`when`(simuleringEndPointRouter.getStillingsprosenter(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(stillingsprosenter)
        val result: List<Stillingsprosent> = callable.call()
        assertStillingsprosenter(stillingsprosenter, result)
    }

    @Test
    @Throws(Exception::class)
    fun call_shall_return_stillingsprosenter_with_rest() {
        val tpOrdning: `var` = TPOrdning("tss1", "tp1")
        val tpLeverandor: `var` = TpLeverandor("lev", "url1", REST)
        val callable: `var` = StillingsprosentCallable("fnr1", tpOrdning, tpLeverandor, simuleringEndPointRouter)
        val stillingsprosenter: List<Stillingsprosent> = prepareStillingsprosenter()
        Mockito.`when`(simuleringEndPointRouter.getStillingsprosenter(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(stillingsprosenter)
        val result: List<Stillingsprosent> = callable.call()
        assertStillingsprosenter(stillingsprosenter, result)
    }

    @Test
    @Throws(Exception::class)
    fun exception_shall_be_rethrown_as_StillingsprosentCallableException_with_soap() {
        val tpOrdning: `var` = TPOrdning("tss1", "tp1")
        val tpLeverandor: `var` = TpLeverandor("lev", "url1", SOAP)
        val callable: `var` = StillingsprosentCallable("fnr1", tpOrdning, tpLeverandor, simuleringEndPointRouter)
        Mockito.`when`(simuleringEndPointRouter.getStillingsprosenter(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenThrow(WebServiceIOException("msg from cause"))
        val exception: StillingsprosentCallableException = Assertions.assertThrows(StillingsprosentCallableException::class.java, Executable { callable.call() })
        assertThat(exception.getMessage(), Matchers.`is`("Call to getStillingsprosenter failed with exception: org.springframework.ws.client.WebServiceIOException: msg from cause"))
        assertThat(exception.getTpOrdning(), `is`(tpOrdning))
    }

    @Test
    @Throws(Exception::class)
    fun exception_shall_be_rethrown_as_StillingsprosentCallableException_with_rest() {
        val tpOrdning: `var` = TPOrdning("tss1", "tp1")
        val tpLeverandor: `var` = TpLeverandor("lev", "url1", REST)
        val callable: `var` = StillingsprosentCallable("fnr1", tpOrdning, tpLeverandor, simuleringEndPointRouter)
        Mockito.`when`(simuleringEndPointRouter.getStillingsprosenter(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenThrow(WebServiceIOException("msg from cause"))
        val exception: StillingsprosentCallableException = Assertions.assertThrows(StillingsprosentCallableException::class.java, Executable { callable.call() })
        assertThat(exception.getMessage(), Matchers.`is`("Call to getStillingsprosenter failed with exception: org.springframework.ws.client.WebServiceIOException: msg from cause"))
        assertThat(exception.getTpOrdning(), `is`(tpOrdning))
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