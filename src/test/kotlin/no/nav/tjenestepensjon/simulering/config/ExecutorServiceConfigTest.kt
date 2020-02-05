package no.nav.tjenestepensjon.simulering.config

import AsyncExecutor.AsyncResponse
import OutgoingResponse.SimulertPensjon
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.Stillingsprosent
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor.EndpointImpl.SOAP
import org.hamcrest.MatcherAssert
import org.hamcrest.core.Is
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import java.util.concurrent.ThreadPoolExecutor

@ExtendWith(MockitoExtension::class)
internal class ExecutorServiceConfigTest {
    @Mock
    private val tpLeverandorConfig: TpLeverandorConfig? = null
    private val executorServiceConfig: ExecutorServiceConfig = ExecutorServiceConfig()
    @Test
    fun createOneThreadPerProvider() {
        Mockito.`when`(tpLeverandorConfig.tpLeverandorList()).thenReturn(List.of(TpLeverandor("lev1", "url1", SOAP), TpLeverandor("lev2", "url2", SOAP)))
        val executorService = executorServiceConfig.taskExecutor(tpLeverandorConfig) as ThreadPoolExecutor
        MatcherAssert.assertThat(executorService.corePoolSize, Is.`is`(2))
    }
}