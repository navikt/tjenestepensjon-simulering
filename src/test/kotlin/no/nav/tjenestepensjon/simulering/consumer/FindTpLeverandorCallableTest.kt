package no.nav.tjenestepensjon.simulering.consumer

import AsyncExecutor.AsyncResponse
import OutgoingResponse.SimulertPensjon
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.Stillingsprosent
import no.nav.tjenestepensjon.simulering.model.v1.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor.EndpointImpl.SOAP
import org.hamcrest.core.Is
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class FindTpLeverandorCallableTest {
    private val tpOrdning: TPOrdning = TPOrdning("1234", "1234")
    private val tpLeverandorMap: List<TpLeverandor> = java.util.List.of<TpLeverandor>(TpLeverandor("tpLeverandorName", "url1", SOAP))
    @Mock
    private val tpConfigConsumer: TpConfigConsumer? = null
    @InjectMocks
    private var callable: FindTpLeverandorCallable? = null

    @Test
    @Throws(Exception::class)
    fun shouldReturnMappedLeverandor() {
        Mockito.`when`(tpConfigConsumer.findTpLeverandor(tpOrdning)).thenReturn("tpLeverandorName")
        callable = FindTpLeverandorCallable(tpOrdning, tpConfigConsumer, tpLeverandorMap)
        assertThat(callable.call().getName(), Is.`is`("tpLeverandorName"))
        assertThat(callable.call().getUrl(), Is.`is`("url1"))
    }
}