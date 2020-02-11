package no.nav.tjenestepensjon.simulering.soap

import no.nav.tjenestepensjon.simulering.consumer.TokenClient
import no.nav.tjenestepensjon.simulering.domain.TokenImpl
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor.EndpointImpl.SOAP
import no.nav.tjenestepensjon.simulering.model.v1.domain.FNR
import no.nav.tjenestepensjon.simulering.model.v1.domain.Stillingsprosent
import no.nav.tjenestepensjon.simulering.model.v1.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.model.v1.request.HentStillingsprosentListeRequest
import no.nav.tjenestepensjon.simulering.model.v1.response.HentStillingsprosentListeResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.ws.client.core.WebServiceTemplate
import java.time.LocalDate

@SpringBootTest(classes = [TokenClient::class, WebServiceTemplate::class, SoapClient::class, SamlConfig::class])
internal class SoapClientTest {

    @MockBean
    lateinit var template: WebServiceTemplate

    @MockBean
    lateinit var tokenClient: TokenClient

    @Autowired
    lateinit var client: SoapClient

    @Test
    @Throws(Exception::class)
    fun stillingsprosenter_shall_return_list() {

        val stillingsprosenter = prepareStillingsprosenter()

        Mockito.`when`(template.marshalSendAndReceive(
                ArgumentMatchers.any<HentStillingsprosentListeRequest>(), ArgumentMatchers.any()))
                .thenReturn(HentStillingsprosentListeResponse(stillingsprosenter))

        Mockito.`when`(tokenClient.samlAccessToken).thenReturn(TokenImpl())

        val result = client.getStillingsprosenter(
                FNR("01011234567"),
                TPOrdning("tssid", "tpid"),
                TpLeverandor("name", "url", SOAP)
        )
        stillingsprosenter.forEachIndexed { index, stillingsprosent -> assertEquals(stillingsprosent, result[index]) }
    }

    companion object {
        private fun prepareStillingsprosenter() = listOf(
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
    }
}