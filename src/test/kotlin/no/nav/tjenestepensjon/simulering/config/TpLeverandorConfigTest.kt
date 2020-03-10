package no.nav.tjenestepensjon.simulering.config

import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


internal class TpLeverandorConfigTest {

    private val tpLeverandorConfig: TpLeverandorConfig = TpLeverandorConfig()

    @Test
    fun `Should create list from delimited string`() {
        tpLeverandorConfig.setLeverandorUrlMap("leverandor,http://www.leverandor.com,SOAP|anotherLeverandor,http://www.another.com,REST")
        val tpLeverandorList: List<TpLeverandor> = tpLeverandorConfig.tpLeverandorList()
        val leverandor = tpLeverandorList.firstOrNull { l: TpLeverandor -> l.name.equals("leverandor", true) }
        val another = tpLeverandorList.firstOrNull { l: TpLeverandor -> l.name.equals("anotherLeverandor", true) }
        assertEquals("leverandor", leverandor?.name)
        assertEquals("http://www.leverandor.com", leverandor?.url)
        assertEquals("anotherLeverandor", another?.name)
        assertEquals("http://www.another.com", another?.url)
    }

    @Test
    fun `Fails when missing provider details`() {
        tpLeverandorConfig.setLeverandorUrlMap("leverandor,http://www.leverandor.com")
        assertThrows<AssertionError> { tpLeverandorConfig.tpLeverandorList() }
    }
}