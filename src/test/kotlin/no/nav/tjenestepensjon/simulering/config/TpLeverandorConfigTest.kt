package no.nav.tjenestepensjon.simulering.config

import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor.EndpointImpl.REST
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor.EndpointImpl.SOAP
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.mock.env.MockEnvironment

internal class TpLeverandorConfigTest {
    private val tpLeverandorConfig: TpLeverandorConfig = TpLeverandorConfig(MockEnvironment().apply {
        setProperty("klp.name", "tp1")
        setProperty("klp.implementation", "SOAP")
        setProperty("klp.simuleringUrl", "simUrl1")
        setProperty("klp.stillingsprosentUrl", "stillingUrl1")
        setProperty("spk.name", "tp2")
        setProperty("spk.implementation", "REST")
        setProperty("spk.simuleringUrl", "simUrl2")
        setProperty("spk.stillingsprosentUrl", "stillingUrl2")
    })

    @Test
    fun `Should create list from delimited string`() {
        tpLeverandorConfig.tpLeverandorList().apply {
            first { l: TpLeverandor -> l.name.equals("tp1", true) }.apply {
                assertEquals("tp1", name)
                assertEquals(SOAP, impl)
                assertEquals("simUrl1", simuleringUrl)
                assertEquals("stillingUrl1", stillingsprosentUrl)
            }
            first { l: TpLeverandor -> l.name.equals("tp2", true) }.apply {
                assertEquals("tp2", name)
                assertEquals(REST, impl)
                assertEquals("simUrl2", simuleringUrl)
                assertEquals("stillingUrl2", stillingsprosentUrl)
            }
        }
    }
}
