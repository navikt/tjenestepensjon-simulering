package no.nav.tjenestepensjon.simulering.config

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor.EndpointImpl.SOAP
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.concurrent.ThreadPoolExecutor

@ExtendWith(MockKExtension::class)
internal class ExecutorServiceConfigTest {

    @MockK
    private lateinit var tpLeverandorConfig: TpLeverandorConfig

    private val executorServiceConfig: ExecutorServiceConfig = ExecutorServiceConfig()

    @Test
    fun `Create one thread per provider`() {
        every { tpLeverandorConfig.tpLeverandorList() } returns listOf(
            TpLeverandor("lev1", SOAP, "sim2", "stilling2"),
            TpLeverandor("lev2", SOAP, "sim2", "stilling2")
        )
        val executorService = executorServiceConfig.taskExecutor(tpLeverandorConfig) as ThreadPoolExecutor
        assertEquals(2, executorService.corePoolSize)
    }
}
