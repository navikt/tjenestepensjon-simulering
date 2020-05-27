package no.nav.tjenestepensjon.simulering.v2.config

import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class TpLeverandorConfigTest {


    private val tpLeverandorConfig: TpLeverandorConfig = TpLeverandorConfig()

    @Test
    fun `Should create list from delimited string`() {
        tpLeverandorConfig.setLeverandorUrlMap("leverandor,http://www.leverandor.com,REST|anotherLeverandor,http://www.another.com,REST,maskinportenIntegrasjon")
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
        org.junit.jupiter.api.assertThrows<AssertionError> { tpLeverandorConfig.tpLeverandorList() }
    }

    @Test
    fun `Does not fails when missing provider details`() {
        tpLeverandorConfig.setLeverandorUrlMap("leverandor,http://www.leverandor.com,SOAP,maskinportenIntegrasjon|leverandor,http://www.leverandor.com,REST")
        tpLeverandorConfig.tpLeverandorList()
    }


}