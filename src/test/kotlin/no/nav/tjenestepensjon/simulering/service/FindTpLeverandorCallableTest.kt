package no.nav.tjenestepensjon.simulering.service

import no.nav.tjenestepensjon.simulering.AppMetrics
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdningIdDto
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor.EndpointImpl.SOAP
import no.nav.tjenestepensjon.simulering.v1.consumer.FindTpLeverandorCallable
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class FindTpLeverandorCallableTest {

    @Mock
    private lateinit var tpClient: TpClient
    @Mock
    private lateinit var appMetrics: AppMetrics

    private val tpOrdningIdDto = TPOrdningIdDto("80001234", "1234")
    private val tpLeverandorMap = listOf(TpLeverandor("tpLeverandorName", SOAP, "simulerUrl", "stillingUrl"))

    @Test
    fun `Should return mapped leverandor`() {
        `when`(tpClient.findTpLeverandorName(tpOrdningIdDto)).thenReturn("tpLeverandorName")
        FindTpLeverandorCallable(tpOrdningIdDto, tpClient, tpLeverandorMap, appMetrics).call().apply {
            assertEquals("tpLeverandorName", name)
            assertEquals(SOAP, impl)
            assertEquals("simulerUrl", simuleringUrl)
            assertEquals("stillingUrl", stillingsprosentUrl)
        }
    }
}
