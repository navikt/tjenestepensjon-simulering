package no.nav.tjenestepensjon.simulering.service

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor.EndpointImpl.SOAP
import no.nav.tjenestepensjon.simulering.v1.consumer.FindTpLeverandorCallable
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class FindTpLeverandorCallableTest {

    @MockK
    private lateinit var tpClient: TpClient

    private val tpOrdning = TPOrdning("80001234", "1234")
    private val tpLeverandorMap = listOf(TpLeverandor("tpLeverandorName", SOAP, "simulerUrl", "stillingUrl"))

    @Test
    fun `Should return mapped leverandor`() {
        every { tpClient.findTpLeverandor(tpOrdning) } returns "tpLeverandorName"
        FindTpLeverandorCallable(tpOrdning, tpClient, tpLeverandorMap).call().apply {
            assertEquals("tpLeverandorName", name)
            assertEquals(SOAP, impl)
            assertEquals("simulerUrl", simuleringUrl)
            assertEquals("stillingUrl", stillingsprosentUrl)
        }
    }
}
