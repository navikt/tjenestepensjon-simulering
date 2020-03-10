package no.nav.tjenestepensjon.simulering.consumer

import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor.EndpointImpl.SOAP
import no.nav.tjenestepensjon.simulering.model.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.v1.consumer.FindTpLeverandorCallable
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension

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
