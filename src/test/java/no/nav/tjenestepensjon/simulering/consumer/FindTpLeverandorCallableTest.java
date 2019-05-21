package no.nav.tjenestepensjon.simulering.consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

import static no.nav.tjenestepensjon.simulering.domain.TpLeverandor.EndpointImpl.SOAP;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.tjenestepensjon.simulering.domain.TPOrdning;
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor;

@ExtendWith(MockitoExtension.class)
class FindTpLeverandorCallableTest {

    private final TPOrdning tpOrdning = new TPOrdning("1234", "1234");
    private final List<TpLeverandor> tpLeverandorMap = List.of(new TpLeverandor("tpLeverandorName", "url1", SOAP));

    @Mock
    private TpConfigConsumer tpConfigConsumer;
    @InjectMocks
    private FindTpLeverandorCallable callable;

    @Test
    void shouldReturnMappedLeverandor() throws Exception {
        when(tpConfigConsumer.findTpLeverandor(tpOrdning)).thenReturn("tpLeverandorName");
        callable = new FindTpLeverandorCallable(tpOrdning, tpConfigConsumer, tpLeverandorMap);
        assertThat(callable.call().getName(), is("tpLeverandorName"));
        assertThat(callable.call().getUrl(), is("url1"));
    }
}