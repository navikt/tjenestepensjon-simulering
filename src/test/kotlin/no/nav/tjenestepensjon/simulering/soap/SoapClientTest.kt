package no.nav.tjenestepensjon.simulering.soap

import no.nav.tjenestepensjon.simulering.consumer.TokenClient
import no.nav.tjenestepensjon.simulering.domain.TokenImpl
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor.EndpointImpl.SOAP
import no.nav.tjenestepensjon.simulering.model.v1.*
import no.nav.tjenestepensjon.simulering.model.v1.domain.FNR
import no.nav.tjenestepensjon.simulering.model.v1.domain.Stillingsprosent
import no.nav.tjenestepensjon.simulering.model.v1.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.model.v1.domain.Utbetalingsperiode
import no.nav.tjenestepensjon.simulering.model.v1.request.HentStillingsprosentListeRequest
import no.nav.tjenestepensjon.simulering.model.v1.response.HentStillingsprosentListeResponse
import no.nav.tjenestepensjon.simulering.model.v1.response.SimulerOffentligTjenestepensjonResponse
import no.nav.tjenestepensjon.simulering.model.v1.response.SimulertPensjon
import no.nav.tjenestepensjon.simulering.soap.marshalling.SOAPAdapter
import no.nav.tjenestepensjon.simulering.soap.marshalling.request.XMLHentStillingsprosentListeRequestWrapper
import no.nav.tjenestepensjon.simulering.soap.marshalling.request.XMLSimulerOffentligTjenestepensjonRequestWrapper
import no.nav.tjenestepensjon.simulering.soap.marshalling.response.XMLHentStillingsprosentListeResponseWrapper
import no.nav.tjenestepensjon.simulering.soap.marshalling.response.XMLSimulerOffentligTjenestepensjonResponseWrapper
import no.nav.tjenestepensjon.simulering.testHelper.anyNonNull
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.ws.client.core.WebServiceTemplate
import java.time.LocalDate
import java.util.Collections.singletonList

@SpringBootTest(classes = [TokenClient::class, WebServiceTemplate::class, SoapClient::class, SamlConfig::class])
internal class SoapClientTest {

    @MockBean
    lateinit var template: WebServiceTemplate

    @MockBean
    lateinit var tokenClient: TokenClient

    @Autowired
    lateinit var client: SoapClient

    @Test
    fun `Stillingsprosenter shall return list`() {
        Mockito.`when`(template.marshalSendAndReceive(
                anyNonNull<XMLHentStillingsprosentListeRequestWrapper>(), anyNonNull<SOAPCallback>()))
                .thenReturn(SOAPAdapter.marshal(defaultHentStillingsprosentListeResponse))

        Mockito.`when`(tokenClient.samlAccessToken).thenReturn(TokenImpl(accessToken = "bogus"))

        val result = client.getStillingsprosenter(
                defaultFNR,
                defaultTPOrdning,
                TpLeverandor("name", "url", SOAP)
        )
        defaultStillingsprosentListe.forEachIndexed { index, stillingsprosent -> assertEquals(stillingsprosent, result[index]) }
    }

    @Test
    fun `SimulerOffentligTjenestepensjon shall return list`() {
        Mockito.`when`(template.marshalSendAndReceive(
                anyNonNull<XMLSimulerOffentligTjenestepensjonRequestWrapper>(), anyNonNull<SOAPCallback>()))
                .thenReturn(SOAPAdapter.marshal(defaultSimulerOffentligTjenestepensjonResponse, defaultFNR))

        Mockito.`when`(tokenClient.samlAccessToken).thenReturn(TokenImpl(accessToken = "bogus"))

        val result = client.simulerPensjon(
                request = defaultSimulerPensjonRequest,
                tpOrdning = defaultTPOrdning,
                tpLeverandor = TpLeverandor("name", "url", SOAP),
                tpOrdningStillingsprosentMap = mapOf(defaultTPOrdning to singletonList(defaultStillingsprosent))
        )
        defaultSimulertPensjonList.forEachIndexed { index, simulertPensjon -> assertEquals(simulertPensjon, result[index]) }
    }
}