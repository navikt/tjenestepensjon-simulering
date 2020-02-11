package no.nav.tjenestepensjon.simulering.consumer

import no.nav.tjenestepensjon.simulering.model.v1.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor.EndpointImpl.SOAP
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.junit.jupiter.api.Assertions.assertEquals

@ExtendWith(MockitoExtension::class)
internal class FindTpLeverandorCallableTest {

    @Mock
    private lateinit var tpConfigConsumer: TpConfigConsumer

    private val tpOrdning: TPOrdning = TPOrdning("1234", "1234")
    private val tpLeverandorMap: List<TpLeverandor> = listOf(TpLeverandor("tpLeverandorName", "url1", SOAP))

    @Test
    fun `Should return mapped leverandor`() {
        Mockito.`when`(tpConfigConsumer.findTpLeverandor(tpOrdning)).thenReturn("tpLeverandorName")
        FindTpLeverandorCallable(tpOrdning, tpConfigConsumer, tpLeverandorMap).call().let {
            assertEquals("tpLeverandorName", it.name)
            assertEquals("url1", it.url)
        }
    }
}
