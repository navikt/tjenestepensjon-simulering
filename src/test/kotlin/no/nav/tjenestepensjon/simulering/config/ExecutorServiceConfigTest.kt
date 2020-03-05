package no.nav.tjenestepensjon.simulering.config

import no.nav.tjenestepensjon.simulering.v1.models.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.v1.models.domain.TpLeverandor.EndpointImpl.SOAP
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import java.util.concurrent.ThreadPoolExecutor
import org.junit.jupiter.api.Assertions.assertEquals

@ExtendWith(MockitoExtension::class)
internal class ExecutorServiceConfigTest {

    @Mock
    private lateinit var tpLeverandorConfig: TpLeverandorConfig

    private val executorServiceConfig: ExecutorServiceConfig = ExecutorServiceConfig()

    @Test
    fun `Create one thread per provider`() {
        Mockito.`when`(tpLeverandorConfig.tpLeverandorList())
                .thenReturn(listOf(TpLeverandor("lev1", "url1", SOAP), TpLeverandor("lev2", "url2", SOAP)))
        val executorService = executorServiceConfig.taskExecutor(tpLeverandorConfig) as ThreadPoolExecutor
        assertEquals(2, executorService.corePoolSize)
    }
}