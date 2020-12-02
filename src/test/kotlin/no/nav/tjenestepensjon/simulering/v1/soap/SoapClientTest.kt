package no.nav.tjenestepensjon.simulering.v1.soap

import no.nav.tjenestepensjon.simulering.config.ObjectMapperConfig
import no.nav.tjenestepensjon.simulering.domain.TokenImpl
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor.EndpointImpl.SOAP
import no.nav.tjenestepensjon.simulering.testHelper.anyNonNull
import no.nav.tjenestepensjon.simulering.v1.consumer.TokenClientOld
import no.nav.tjenestepensjon.simulering.v1.models.*
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.SOAPAdapter
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.request.XMLHentStillingsprosentListeRequestWrapper
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.request.XMLSimulerOffentligTjenestepensjonRequestWrapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.ws.client.core.WebServiceTemplate


@SpringBootTest(classes = [TokenClientOld::class, WebServiceTemplate::class, SoapClient::class, SamlConfig::class, ObjectMapperConfig::class])
internal class SoapClientTest {

    @MockBean
    lateinit var template: WebServiceTemplate

    @MockBean
    lateinit var tokenClientOld: TokenClientOld

    @Autowired
    lateinit var client: SoapClient

    @Test
    fun `Stillingsprosenter shall return list`() {
        Mockito.`when`(template.marshalSendAndReceive(
                anyNonNull<XMLHentStillingsprosentListeRequestWrapper>(), anyNonNull<SOAPCallback>()))
                .thenReturn(SOAPAdapter.marshal(defaultHentStillingsprosentListeResponse))

        Mockito.`when`(tokenClientOld.samlAccessToken).thenReturn(TokenImpl("bogus", 0))

        val result = client.getStillingsprosenter(
                defaultFNR,
                defaultTPOrdning,
                TpLeverandor("name", SOAP, "sim", "stilling")
        )
        defaultStillingsprosentListe.forEachIndexed { index, stillingsprosent -> assertEquals(stillingsprosent, result[index]) }
    }

    @Test
    fun `SimulerOffentligTjenestepensjon shall return list`() {
        Mockito.`when`(template.marshalSendAndReceive(
                anyNonNull<XMLSimulerOffentligTjenestepensjonRequestWrapper>(), anyNonNull<SOAPCallback>()))
                .thenReturn(SOAPAdapter.marshal(defaultSimulerOffentligTjenestepensjonResponse, defaultFNR))

        Mockito.`when`(tokenClientOld.samlAccessToken).thenReturn(TokenImpl("bogus", 0))

        val result = client.simulerPensjon(
                request = defaultSimulerPensjonRequest,
                tpOrdning = defaultTPOrdning,
                tpLeverandor = TpLeverandor("name", SOAP, "sim", "stilling"),
                tpOrdningStillingsprosentMap = mapOf(defaultTPOrdning to listOf(defaultStillingsprosent))
        )
        defaultSimulertPensjonList.forEachIndexed { index, simulertPensjon -> assertEquals(simulertPensjon, result[index]) }
    }
}