package no.nav.tjenestepensjon.simulering.config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static no.nav.tjenestepensjon.simulering.domain.TpLeverandor.EndpointImpl.REST;
import static no.nav.tjenestepensjon.simulering.domain.TpLeverandor.EndpointImpl.SOAP;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.nav.tjenestepensjon.simulering.domain.TpLeverandor;

class TpLeverandorConfigTest {
    private TpLeverandorConfig tpLeverandorConfig = new TpLeverandorConfig();

    @BeforeEach
    void beforeEach() {
        tpLeverandorConfig.setLeverandorUrlMap("leverandor,http://www.leverandor.com,SOAP|anotherLeverandor,http://www.another.com,REST");
    }

    @Test
    void shouldCreateListFromDelimitedString() {
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
        tpLeverandorConfig.setLeverandorUrlMap("leverandor,http://www.leverandor.com");

        assertThrows(AssertionError.class, () -> tpLeverandorConfig.tpLeverandorList());
    }

    @Test
    void failsWhenGivenNonExistingEndPointImpl() {
        tpLeverandorConfig.setLeverandorUrlMap("leverandor,http://www.leverandor.com,STRESS");

        assertThrows(IllegalArgumentException.class, () -> tpLeverandorConfig.tpLeverandorList());
    }
}
