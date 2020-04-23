package no.nav.tjenestepensjon.simulering.config

import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


internal class TpLeverandorConfigOldTest {

    private val tpLeverandorConfigOld: TpLeverandorConfigOld = TpLeverandorConfigOld()

    @Test
    fun `Should create list from delimited string`() {
        tpLeverandorConfigOld.setLeverandorUrlMap("leverandor,http://www.leverandor.com,SOAP|anotherLeverandor,http://www.another.com,REST")
        val tpLeverandorList: List<TpLeverandor> = tpLeverandorConfigOld.tpLeverandorListOld()
        val leverandor = tpLeverandorList.firstOrNull { l: TpLeverandor -> l.name.equals("leverandor", true) }
        val another = tpLeverandorList.firstOrNull { l: TpLeverandor -> l.name.equals("anotherLeverandor", true) }
        assertEquals("leverandor", leverandor?.name)
        assertEquals("http://www.leverandor.com", leverandor?.url)
        assertEquals("anotherLeverandor", another?.name)
        assertEquals("http://www.another.com", another?.url)
    }

    @Test
    fun `Fails when missing provider details`() {
        tpLeverandorConfigOld.setLeverandorUrlMap("leverandor,http://www.leverandor.com")
        assertThrows<AssertionError> { tpLeverandorConfigOld.tpLeverandorListOld() }
    }
}