package no.nav.tjenestepensjon.simulering.v1.soap

import no.nav.tjenestepensjon.simulering.config.ObjectMapperConfig
import no.nav.tjenestepensjon.simulering.defaultFNRString
import no.nav.tjenestepensjon.simulering.defaultTPOrdningIdDto
import no.nav.tjenestepensjon.simulering.domain.TokenImpl
import no.nav.tjenestepensjon.simulering.sporingslogg.SporingsloggService
import no.nav.tjenestepensjon.simulering.testHelper.anyNonNull
import no.nav.tjenestepensjon.simulering.v1.consumer.GatewayTokenClient
import no.nav.tjenestepensjon.simulering.v1.models.defaultHentStillingsprosentListeResponse
import no.nav.tjenestepensjon.simulering.v1.models.defaultStillingsprosentListe
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.SOAPAdapter
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.request.XMLHentStillingsprosentListeRequestWrapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.ws.client.core.WebServiceTemplate


@SpringBootTest(classes = [WebServiceTemplate::class, SPKStillingsprosentSoapClient::class, SamlConfig::class, ObjectMapperConfig::class])
internal class SPKStillingsprosentSoapClientTest {

    @MockitoBean
    lateinit var template: WebServiceTemplate

    @MockitoBean
    lateinit var gatewayTokenClient: GatewayTokenClient

    @MockitoBean
    lateinit var sporingsloggService: SporingsloggService

    @Autowired
    lateinit var client: SPKStillingsprosentSoapClient

    @Test
    fun `Stillingsprosenter shall return list`() {
        `when`(
            template.marshalSendAndReceive(
                anyNonNull<XMLHentStillingsprosentListeRequestWrapper>(), anyNonNull<SOAPCallback>()
            )
        ).thenReturn(SOAPAdapter.marshal(defaultHentStillingsprosentListeResponse))

        `when`(gatewayTokenClient.samlAccessToken).thenReturn(TokenImpl("bogus", 0))

        val result = client.getStillingsprosenter(defaultFNRString, defaultTPOrdningIdDto)
        defaultStillingsprosentListe.forEachIndexed { index, stillingsprosent ->
            assertEquals(stillingsprosent, result[index])
        }
    }
}
