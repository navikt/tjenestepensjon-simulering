package no.nav.tjenestepensjon.simulering.config

import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor.EndpointImpl.SOAP
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.util.concurrent.ThreadPoolExecutor

@ExtendWith(MockitoExtension::class)
internal class ExecutorServiceConfigTest {

    @Mock
    private lateinit var tpLeverandorConfig: TpLeverandorConfig

    private val executorServiceConfig: ExecutorServiceConfig = ExecutorServiceConfig()

    @Test
    fun `Create one thread per provider`() {
        `when`(tpLeverandorConfig.tpLeverandorList()).thenReturn(
            listOf(
                TpLeverandor("lev1", SOAP, "sim2", "stilling2"), TpLeverandor("lev2", SOAP, "sim2", "stilling2")
            )
        )
        val executorService = executorServiceConfig.taskExecutor(tpLeverandorConfig) as ThreadPoolExecutor
        assertEquals(2, executorService.corePoolSize)
    }
}
