package no.nav.tjenestepensjon.simulering.config

import AsyncExecutor.AsyncResponse
import OutgoingResponse.SimulertPensjon
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.Stillingsprosent
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor.EndpointImpl.REST
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor.EndpointImpl.SOAP
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

internal class TpLeverandorConfigTest {
    private val tpLeverandorConfig: TpLeverandorConfig = TpLeverandorConfig()
    @BeforeEach
    fun beforeEach() {
        tpLeverandorConfig.setLeverandorUrlMap("leverandor,http://www.leverandor.com,SOAP|anotherLeverandor,http://www.another.com,REST")
    }

    @Test
    fun shouldCreateListFromDelimitedString() {
        val tpLeverandorList: List<TpLeverandor> = tpLeverandorConfig.tpLeverandorList()
        val leverandor: Optional<TpLeverandor> = tpLeverandorList.stream().filter { l: TpLeverandor -> l.getName().equalsIgnoreCase("leverandor") }.findAny()
        val another: Optional<TpLeverandor> = tpLeverandorList.stream().filter { l: TpLeverandor -> l.getName().equalsIgnoreCase("anotherLeverandor") }.findAny()
        MatcherAssert.assertThat(leverandor.isPresent(), Matchers.`is`(true))
        assertThat(leverandor.get().getName(), Matchers.`is`("leverandor"))
        assertThat(leverandor.get().getUrl(), Matchers.`is`("http://www.leverandor.com"))
        assertThat(leverandor.get().getImpl(), `is`(SOAP))
        MatcherAssert.assertThat(another.isPresent(), Matchers.`is`(true))
        assertThat(another.get().getName(), Matchers.`is`("anotherLeverandor"))
        assertThat(another.get().getUrl(), Matchers.`is`("http://www.another.com"))
        assertThat(another.get().getImpl(), `is`(REST))
    }

    @Test
    fun failsWhenMissingProviderDetails() {
        tpLeverandorConfig.setLeverandorUrlMap("leverandor,http://www.leverandor.com")
        Assertions.assertThrows(AssertionError::class.java) { tpLeverandorConfig.tpLeverandorList() }
    }

    @Test
    fun failsWhenGivenNonExistingEndPointImpl() {
        tpLeverandorConfig.setLeverandorUrlMap("leverandor,http://www.leverandor.com,STRESS")
        Assertions.assertThrows(IllegalArgumentException::class.java) { tpLeverandorConfig.tpLeverandorList() }
    }
}