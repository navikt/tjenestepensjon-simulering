package no.nav.tjenestepensjon.simulering.soap

import AsyncExecutor.AsyncResponse
import OutgoingResponse.SimulertPensjonimport
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.Stillingsprosent
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.v1.HentStillingsprosentListe
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.v1.HentStillingsprosentListeResponse
import no.nav.tjenestepensjon.simulering.consumer.TokenClient
import no.nav.tjenestepensjon.simulering.model.v1.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.domain.TokenImpl
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor.EndpointImpl.SOAP
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.springframework.ws.client.core.WebServiceTemplate
import java.lang.Exceptionimport
import java.time.LocalDate
import javax.xml.datatype.XMLGregorianCalendar

org.junit.jupiter.api.*import java.lang.Exceptionimport

java.util.*

internal class SoapClientTest {
    @get:Throws(Exception::class)
    @get:Test
    val stillingsprosenter_shall_return_list: Unit
        get() {
            val template: `var` = Mockito.mock(WebServiceTemplate::class.java)
            val tokenClient: `var` = Mockito.mock(TokenClient::class.java)
            val stillingsprosenter: List<Stillingsprosent> = prepareStillingsprosenter()
            Mockito.`when`(template.marshalSendAndReceive(ArgumentMatchers.any(HentStillingsprosentListe::class.java), ArgumentMatchers.any())).thenReturn(TestResponse(stillingsprosenter))
            Mockito.`when`(tokenClient.getSamlAccessToken()).thenReturn(TokenImpl())
            val client = SoapClient(template, tokenClient)
            val result: List<Stillingsprosent> = client.getStillingsprosenter("fnr1", TPOrdning("tssid", "tpid"), TpLeverandor("name", "url", SOAP))
            assertStillingsprosenter(result, stillingsprosenter)
        }

    private class StillingsprosentBuilder {
        private val stillingsprosent: Stillingsprosent = Stillingsprosent()
        fun stillingsprosent(value: Double): StillingsprosentBuilder {
            stillingsprosent.setStillingsprosent(value)
            return this
        }

        fun datoFom(year: Int, month: Int, day: Int): StillingsprosentBuilder {
            val calendar = Mockito.mock(XMLGregorianCalendar::class.java)
            Mockito.`when`(calendar.year).thenReturn(year)
            Mockito.`when`(calendar.month).thenReturn(month)
            Mockito.`when`(calendar.day).thenReturn(day)
            stillingsprosent.setDatoFom(calendar)
            return this
        }

        fun datoTom(year: Int, month: Int, day: Int): StillingsprosentBuilder {
            val calendar = Mockito.mock(XMLGregorianCalendar::class.java)
            Mockito.`when`(calendar.year).thenReturn(year)
            Mockito.`when`(calendar.month).thenReturn(month)
            Mockito.`when`(calendar.day).thenReturn(day)
            stillingsprosent.setDatoTom(calendar)
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

    private inner class TestResponse internal constructor(stillingsprosenter: List<Stillingsprosent>?) : HentStillingsprosentListeResponse() {
        init {
            response = HentStillingsprosentListeResponse()
            response.getStillingsprosentListe().addAll(stillingsprosenter)
        }
    }

    companion object {
        private fun assertStillingsprosenter(expected: List<Stillingsprosent>,
                                             actual: List<Stillingsprosent>) {
            Assertions.assertEquals(expected.size, actual.size)
            for (index in expected.indices) {
                assertStillingsprosent(expected[index], actual[index])
            }
        }

        private fun assertStillingsprosent(expected: Stillingsprosent, actual: Stillingsprosent) {
            assertEquals(expected.getStillingsprosent(), actual.getStillingsprosent())
            assertDate(expected.getDatoFom(), actual.getDatoFom())
            assertDate(expected.getDatoTom(), actual.getDatoTom())
            assertEquals(expected.getFaktiskHovedlonn(), actual.getFaktiskHovedlonn())
            assertEquals(expected.getStillingsuavhengigTilleggslonn(), actual.getStillingsuavhengigTilleggslonn())
            assertEquals(expected.getAldersgrense(), actual.getAldersgrense())
        }

        private fun assertDate(expected: LocalDate, actual: XMLGregorianCalendar) {
            Assertions.assertEquals(expected.year, actual.year)
            Assertions.assertEquals(expected.month.value, actual.month)
            Assertions.assertEquals(expected.dayOfMonth, actual.day)
        }

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
            return Arrays.asList(stillingsprosent1, stillingsprosent2)
        }
    }
}