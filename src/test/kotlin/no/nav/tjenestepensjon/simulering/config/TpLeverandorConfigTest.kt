package no.nav.tjenestepensjon.simulering.config

import no.nav.tjenestepensjon.simulering.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor.EndpointImpl.REST
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor.EndpointImpl.SOAP
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Assertions.assertEquals


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
        assertEquals(SOAP, leverandor?.impl)
        assertEquals("anotherLeverandor", another?.name)
        assertEquals("http://www.another.com", another?.url)
        assertEquals(REST, another?.impl)
    }

    @Test
    fun `Fails when missing provider details`() {
        tpLeverandorConfig.setLeverandorUrlMap("leverandor,http://www.leverandor.com")
        assertThrows<AssertionError> { tpLeverandorConfig.tpLeverandorList() }
    }

    @Test
    fun `Fails when given non existing end point impl`() {
        tpLeverandorConfig.setLeverandorUrlMap("leverandor,http://www.leverandor.com,STRESS")
        assertThrows<IllegalArgumentException> { tpLeverandorConfig.tpLeverandorList() }
    }
}