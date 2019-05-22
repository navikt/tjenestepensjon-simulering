package no.nav.tjenestepensjon.simulering.config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

import static no.nav.tjenestepensjon.simulering.domain.TpLeverandor.EndpointImpl.SOAP;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.tjenestepensjon.simulering.domain.TpLeverandor;

@ExtendWith(MockitoExtension.class)
class ExecutorServiceConfigTest {

    @Mock
    private TpLeverandorConfig tpLeverandorConfig;
    private ExecutorServiceConfig executorServiceConfig = new ExecutorServiceConfig();

    @Test
    void createOneThreadPerProvider() {
        when(tpLeverandorConfig.tpLeverandorList()).thenReturn(List.of(new TpLeverandor("lev1", "url1", SOAP), new TpLeverandor("lev2", "url2", SOAP)));

        ThreadPoolExecutor executorService = (ThreadPoolExecutor) executorServiceConfig.taskExecutor(tpLeverandorConfig);
        assertThat(executorService.getCorePoolSize(), is(2));
    }
}