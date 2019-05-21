package no.nav.tjenestepensjon.simulering.config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import static no.nav.tjenestepensjon.simulering.domain.TpLeverandor.EndpointImpl.REST;
import static no.nav.tjenestepensjon.simulering.domain.TpLeverandor.EndpointImpl.SOAP;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.tjenestepensjon.simulering.domain.TpLeverandor;

@ExtendWith(MockitoExtension.class)
class TpLeverandorConfigTest {

    @Mock
    private TpLeverandorConfig tpLeverandorConfig;

    @Test
    void shouldCreateListFromDelimitedString() {
        when(tpLeverandorConfig.getLeverandorUrlMap()).thenReturn("leverandor,http://www.leverandor.com,SOAP|anotherLeverandor,http://www.another.com,REST");
        when(tpLeverandorConfig.tpLeverandorList()).thenCallRealMethod();

        List<TpLeverandor> tpLeverandorList = tpLeverandorConfig.tpLeverandorList();
        Optional<TpLeverandor> leverandor = tpLeverandorList.stream().filter(l -> l.getName().equalsIgnoreCase("leverandor")).findAny();
        Optional<TpLeverandor> another = tpLeverandorList.stream().filter(l -> l.getName().equalsIgnoreCase("anotherLeverandor")).findAny();

        assertThat(leverandor.isPresent(), is(true));
        assertThat(leverandor.get().getName(), is("leverandor"));
        assertThat(leverandor.get().getUrl(), is("http://www.leverandor.com"));
        assertThat(leverandor.get().getImpl(), is(SOAP));

        assertThat(another.isPresent(), is(true));
        assertThat(another.get().getName(), is("anotherLeverandor"));
        assertThat(another.get().getUrl(), is("http://www.another.com"));
        assertThat(another.get().getImpl(), is(REST));
    }

    @Test
    void failsWhenMissingProviderDetails() {
        when(tpLeverandorConfig.getLeverandorUrlMap()).thenReturn("leverandor,http://www.leverandor.com");
        when(tpLeverandorConfig.tpLeverandorList()).thenCallRealMethod();

        assertThrows(AssertionError.class, () -> tpLeverandorConfig.tpLeverandorList());
    }
}
