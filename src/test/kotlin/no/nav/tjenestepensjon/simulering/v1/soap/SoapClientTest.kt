package no.nav.tjenestepensjon.simulering.v1.soap

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import no.nav.tjenestepensjon.simulering.config.JsonMapperConfig
import no.nav.tjenestepensjon.simulering.defaultFNR
import no.nav.tjenestepensjon.simulering.defaultTPOrdning
import no.nav.tjenestepensjon.simulering.domain.TokenImpl
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor.EndpointImpl.SOAP
import no.nav.tjenestepensjon.simulering.v1.consumer.TokenClientOld
import no.nav.tjenestepensjon.simulering.v1.models.*
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.SOAPAdapter
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.request.XMLHentStillingsprosentListeRequestWrapper
import no.nav.tjenestepensjon.simulering.v1.soap.marshalling.request.XMLSimulerOffentligTjenestepensjonRequestWrapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.ws.client.core.WebServiceTemplate


@SpringBootTest(classes = [TokenClientOld::class, WebServiceTemplate::class, SoapClient::class, SamlConfig::class, JsonMapperConfig::class])
internal class SoapClientTest {

    @MockkBean(relaxed = true)
    lateinit var template: WebServiceTemplate

    @MockkBean
    lateinit var tokenClientOld: TokenClientOld

    @Autowired
    lateinit var client: SoapClient

    @Test
    fun `Stillingsprosenter shall return list`() {
        every {
            template.marshalSendAndReceive(any<XMLHentStillingsprosentListeRequestWrapper>(), any())
        } returns SOAPAdapter.marshal(defaultHentStillingsprosentListeResponse)

        every { tokenClientOld.samlAccessToken } returns TokenImpl("bogus", 0)

        val result = client.getStillingsprosenter(
            defaultFNR, defaultTPOrdning, TpLeverandor("name", SOAP, "sim", "stilling")
        )
        defaultStillingsprosentListe.forEachIndexed { index, stillingsprosent ->
            assertEquals(stillingsprosent, result[index])
        }
    }

    @Test
    fun `SimulerOffentligTjenestepensjon shall return list`() {
        every {
            template.marshalSendAndReceive(any<XMLSimulerOffentligTjenestepensjonRequestWrapper>(), any())
        } returns SOAPAdapter.marshal(defaultSimulerOffentligTjenestepensjonResponse, defaultFNR)

        every { tokenClientOld.samlAccessToken } returns TokenImpl("bogus", 0)

        val result = client.simulerPensjon(
            request = defaultSimulerPensjonRequest,
            tpOrdning = defaultTPOrdning,
            tpLeverandor = TpLeverandor("name", SOAP, "sim", "stilling"),
            tpOrdningStillingsprosentMap = mapOf(defaultTPOrdning to listOf(defaultStillingsprosent))
        )
        defaultSimulertPensjonList.forEachIndexed { index, simulertPensjon ->
            assertEquals(simulertPensjon, result[index])
        }
    }
}
