package no.nav.tjenestepensjon.simulering;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static no.nav.tjenestepensjon.simulering.AsyncExecutor.AsyncResponse;
import static no.nav.tjenestepensjon.simulering.domain.TpLeverandor.EndpointImpl.SOAP;
import static no.nav.tjenestepensjon.simulering.rest.OutgoingResponse.SimulertPensjon;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.tjenestepensjon.simulering.consumer.TpRegisterConsumer;
import no.nav.tjenestepensjon.simulering.domain.Stillingsprosent;
import no.nav.tjenestepensjon.simulering.domain.TPOrdning;
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor;
import no.nav.tjenestepensjon.simulering.exceptions.DuplicateStillingsprosentEndDateException;
import no.nav.tjenestepensjon.simulering.exceptions.MissingStillingsprosentException;
import no.nav.tjenestepensjon.simulering.exceptions.NoTpOrdningerFoundException;
import no.nav.tjenestepensjon.simulering.exceptions.StillingsprosentCallableException;
import no.nav.tjenestepensjon.simulering.rest.IncomingRequest;
import no.nav.tjenestepensjon.simulering.rest.OutgoingResponse;
import no.nav.tjenestepensjon.simulering.service.SimpleSimuleringService;
import no.nav.tjenestepensjon.simulering.service.StillingsprosentResponse;
import no.nav.tjenestepensjon.simulering.service.StillingsprosentService;

@ExtendWith(MockitoExtension.class)
class SimpleSimuleringServiceTest {

    @Mock
    private TpRegisterConsumer tpRegisterConsumer;
    @Mock
    private AsyncExecutor asyncExecutor;
    @Mock
    private StillingsprosentService stillingsprosentService;
    @Mock
    private TjenestepensjonsimuleringEndpointRouter simuleringEnpointRouter;
    @Mock
    private AppMetrics metrics;
    @InjectMocks
    private SimpleSimuleringService simuleringService;

    private IncomingRequest request = new IncomingRequest();

    @BeforeEach
    void beforeEach() {
        request.setFnr("1234");
    }

    @Test
    void shouldReturnStatusAndFeilkodeWhenDuplicateStillingsprosentEndDate() throws Exception {
        TPOrdning tpOrdning = new TPOrdning("1", "1");
        TpLeverandor tpLeverandor = new TpLeverandor("lev1", "url1", SOAP);

        when(tpRegisterConsumer.getTpOrdningerForPerson(any())).thenReturn(List.of(tpOrdning));
        StillingsprosentResponse stillingsprosentResponse = mock(StillingsprosentResponse.class);
        when(stillingsprosentResponse.getTpOrdningStillingsprosentMap()).thenReturn(Map.of(tpOrdning, List.of(new Stillingsprosent())));
        when(stillingsprosentService.getStillingsprosentListe(any(), any())).thenReturn(stillingsprosentResponse);
        AsyncResponse<TPOrdning, TpLeverandor> asyncResponse = new AsyncResponse<>();
        asyncResponse.getResultMap().put(tpOrdning, tpLeverandor);
        when(asyncExecutor.executeAsync(any())).thenReturn(asyncResponse);

        when(stillingsprosentService.getLatestFromStillingsprosent(any(Map.class))).thenThrow(new DuplicateStillingsprosentEndDateException("exception"));

        OutgoingResponse response = simuleringService.simuler(request);
        SimulertPensjon simulertPensjon = response.getSimulertPensjonListe().get(0);
        assertThat(simulertPensjon, is(notNullValue()));
        assertThat(simulertPensjon.getStatus(), is("FEIL"));
        assertThat(simulertPensjon.getFeilkode(), is("PARF"));
    }

    @Test
    void shouldReturnStatusAndFeilkodeWhenMissingStillingsprosent() throws Exception {
        TPOrdning tpOrdning = new TPOrdning("1", "1");
        TpLeverandor tpLeverandor = new TpLeverandor("lev1", "url1", SOAP);

        when(tpRegisterConsumer.getTpOrdningerForPerson(any())).thenReturn(List.of(tpOrdning));
        StillingsprosentResponse stillingsprosentResponse = mock(StillingsprosentResponse.class);
        when(stillingsprosentResponse.getTpOrdningStillingsprosentMap()).thenReturn(Map.of(tpOrdning, List.of(new Stillingsprosent())));
        when(stillingsprosentService.getStillingsprosentListe(any(), any())).thenReturn(stillingsprosentResponse);
        AsyncResponse<TPOrdning, TpLeverandor> asyncResponse = new AsyncResponse<>();
        asyncResponse.getResultMap().put(tpOrdning, tpLeverandor);
        when(asyncExecutor.executeAsync(any())).thenReturn(asyncResponse);

        when(stillingsprosentService.getLatestFromStillingsprosent(any(Map.class))).thenThrow(new MissingStillingsprosentException("exception"));

        OutgoingResponse response = simuleringService.simuler(request);
        SimulertPensjon simulertPensjon = response.getSimulertPensjonListe().get(0);
        assertThat(simulertPensjon, is(notNullValue()));
        assertThat(simulertPensjon.getStatus(), is("FEIL"));
        assertThat(simulertPensjon.getFeilkode(), is("IKKE"));
    }

    @Test
    void shouldAddResponseInfoWhenNoStillingsprosentAvailable() {
        when(asyncExecutor.executeAsync(any())).thenReturn(new AsyncResponse());
        when(stillingsprosentService.getStillingsprosentListe(any(), any())).thenReturn(mock(StillingsprosentResponse.class));

        OutgoingResponse response = simuleringService.simuler(request);
        assertThat(response.getSimulertPensjonListe().get(0).getStatus(), is("FEIL"));
    }

    @Test
    void shouldAddResponseInfoWhenNoTpOrdningFound() throws Exception {
        when(tpRegisterConsumer.getTpOrdningerForPerson(any())).thenThrow(new NoTpOrdningerFoundException("exception"));

        OutgoingResponse response = simuleringService.simuler(request);
        assertThat(response.getSimulertPensjonListe().get(0).getStatus(), is("FEIL"));
    }

    @Test
    void shouldAddResponseInfoWhenSimuleringOk() {
        when(asyncExecutor.executeAsync(any())).thenReturn(new AsyncResponse());
        Map<TPOrdning, List<Stillingsprosent>> map = Map.of(new TPOrdning("tssInkluder", "tpInkluder"), List.of(new Stillingsprosent()));
        List<ExecutionException> exceptions = List.of(new ExecutionException(new StillingsprosentCallableException("msg", null, new TPOrdning("tssUtelatt", "tpUtelatt"))));
        StillingsprosentResponse stillingsprosentResponse = new StillingsprosentResponse(map, exceptions);
        when(stillingsprosentService.getStillingsprosentListe(any(), any())).thenReturn(stillingsprosentResponse);
        when(simuleringEnpointRouter.simulerPensjon(any(), any(), any(), any())).thenReturn(List.of(new SimulertPensjon()));

        OutgoingResponse response = simuleringService.simuler(request);
        assertThat(response.getSimulertPensjonListe().get(0).getUtelatteTpnr().contains("tpUtelatt"), is(true));
        assertThat(response.getSimulertPensjonListe().get(0).getInkluderteTpnr().contains("tpInkluder"), is(true));
        assertThat(response.getSimulertPensjonListe().get(0).getStatus(), is("UFUL"));
    }
}