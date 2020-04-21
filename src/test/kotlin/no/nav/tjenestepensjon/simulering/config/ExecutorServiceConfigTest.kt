package no.nav.tjenestepensjon.simulering.config

import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor.EndpointImpl.SOAP
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import java.util.concurrent.ThreadPoolExecutor

@ExtendWith(MockitoExtension::class)
internal class ExecutorServiceConfigTest {

    @Mock
    private lateinit var tpLeverandorConfigOld: TpLeverandorConfigOld

    private val executorServiceConfig: ExecutorServiceConfig = ExecutorServiceConfig()

    @Test
    fun `Create one thread per provider`() {
        Mockito.`when`(tpLeverandorConfigOld.tpLeverandorList())
                .thenReturn(listOf(TpLeverandor("lev1", "url1", SOAP), TpLeverandor("lev2", "url2", SOAP)))
        val executorService = executorServiceConfig.taskExecutor(tpLeverandorConfigOld) as ThreadPoolExecutor
        assertEquals(2, executorService.corePoolSize)
    }
}