package no.nav.tjenestepensjon.simulering.config

import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor.EndpointImpl.REST
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor.EndpointImpl.SOAP
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


internal class TpLeverandorConfigTest {

    private val tpLeverandorConfig: TpLeverandorConfig = TpLeverandorConfig()

    @Test
    fun `Should create list from delimited string`() {
        tpLeverandorConfig.setLeverandorUrlMap("tp1,SOAP,simUrl1,stillingUrl1|tp2,REST,simUrl2,stillingUrl2")
        val tpLeverandorList: List<TpLeverandor> = tpLeverandorConfig.tpLeverandorList()
        val tpLeverandor1 = tpLeverandorList.firstOrNull { l: TpLeverandor -> l.name.equals("tp1", true) }
        val tpLeverandor2 = tpLeverandorList.firstOrNull { l: TpLeverandor -> l.name.equals("tp2", true) }
        assertEquals("tp1", tpLeverandor1?.name)
        assertEquals(SOAP, tpLeverandor1?.impl)
        assertEquals("simUrl1", tpLeverandor1?.simuleringUrl)
        assertEquals("stillingUrl1", tpLeverandor1?.stillingsprosentUrl)
        assertEquals("tp2", tpLeverandor2?.name)
        assertEquals(REST, tpLeverandor2?.impl)
        assertEquals("simUrl2", tpLeverandor2?.simuleringUrl)
        assertEquals("stillingUrl2", tpLeverandor2?.stillingsprosentUrl)
    }

    @Test
    fun `Fails when missing provider details`() {
        tpLeverandorConfig.setLeverandorUrlMap("leverandor,http://www.leverandor.com")
        assertThrows<AssertionError> { tpLeverandorConfig.tpLeverandorList() }
    }
}