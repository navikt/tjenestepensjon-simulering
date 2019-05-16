package no.nav.tjenestepensjon.simulering;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static no.nav.tjenestepensjon.simulering.rest.OutgoingResponse.SimulertPensjon;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.tjenestepensjon.simulering.domain.Stillingsprosent;
import no.nav.tjenestepensjon.simulering.domain.TPOrdning;
import no.nav.tjenestepensjon.simulering.exceptions.DuplicateStillingsprosentEndDateException;
import no.nav.tjenestepensjon.simulering.exceptions.MissingStillingsprosentException;
import no.nav.tjenestepensjon.simulering.exceptions.NoTpOrdningerFoundException;
import no.nav.tjenestepensjon.simulering.rest.IncomingRequest;
import no.nav.tjenestepensjon.simulering.rest.OutgoingResponse;
import no.nav.tjenestepensjon.simulering.service.SimpleSimuleringService;
import no.nav.tjenestepensjon.simulering.service.StillingsprosentResponse;
import no.nav.tjenestepensjon.simulering.service.StillingsprosentService;

@ExtendWith(MockitoExtension.class)
class SimpleSimuleringServiceTest {

    @Mock
    private StillingsprosentService stillingsprosentService;
    @InjectMocks
    private SimpleSimuleringService simuleringService;

    @Test
    void shouldReturnStatusAndFeilkodeWhenDuplicateStillingsprosentEndDate() throws Exception {
        IncomingRequest request = new IncomingRequest();
        request.setFnr("1234");

        StillingsprosentResponse mockStillingsprosentResponse = mock(StillingsprosentResponse.class);
        when(mockStillingsprosentResponse.getTpOrdningListMap()).thenReturn(Map.of(new TPOrdning("1", "1"), List.of(new Stillingsprosent())));

        when(stillingsprosentService.getStillingsprosentListe(request.getFnr())).thenReturn(mockStillingsprosentResponse);
        when(stillingsprosentService.getLatestFromStillingsprosent(any(Map.class))).thenThrow(new DuplicateStillingsprosentEndDateException("exception"));

        OutgoingResponse response = simuleringService.simuler(request);
        SimulertPensjon simulertPensjon = response.getSimulertPensjonListe().get(0);
        assertThat(simulertPensjon, is(notNullValue()));
        assertThat(simulertPensjon.getStatus(), is("FEIL"));
        assertThat(simulertPensjon.getFeilkode(), is("PARF"));
    }

    @Test
    void shouldReturnStatusAndFeilkodeWhenMissingStillingsprosent() throws Exception {
        IncomingRequest request = new IncomingRequest();
        request.setFnr("1234");

        StillingsprosentResponse mockStillingsprosentResponse = mock(StillingsprosentResponse.class);
        when(mockStillingsprosentResponse.getTpOrdningListMap()).thenReturn(Map.of(new TPOrdning("1", "1"), List.of(new Stillingsprosent())));

        when(stillingsprosentService.getStillingsprosentListe(request.getFnr())).thenReturn(mockStillingsprosentResponse);
        when(stillingsprosentService.getLatestFromStillingsprosent(any(Map.class))).thenThrow(new MissingStillingsprosentException("exception"));

        OutgoingResponse response = simuleringService.simuler(request);
        SimulertPensjon simulertPensjon = response.getSimulertPensjonListe().get(0);
        assertThat(simulertPensjon, is(notNullValue()));
        assertThat(simulertPensjon.getStatus(), is("FEIL"));
        assertThat(simulertPensjon.getFeilkode(), is("IKKE"));
    }

    @Test
    void shouldThrowNullpointerIfNoTpOrdningAnswersStillingsprosent() throws Exception {
        IncomingRequest request = new IncomingRequest();
        request.setFnr("1234");

        StillingsprosentResponse mockStillingsprosentResponse = mock(StillingsprosentResponse.class);

        when(stillingsprosentService.getStillingsprosentListe(request.getFnr())).thenReturn(mockStillingsprosentResponse);

        assertThrows(NullPointerException.class, () -> simuleringService.simuler(request));
    }

    @Test
    void shouldReturnEmptyResponseWhenNoTpOrdningerFound() throws Exception {
        IncomingRequest request = new IncomingRequest();
        request.setFnr("1234");

        when(stillingsprosentService.getStillingsprosentListe(request.getFnr())).thenThrow(new NoTpOrdningerFoundException("exception"));

        OutgoingResponse response = simuleringService.simuler(request);
        assertThat(response.getSimulertPensjonListe(), is(nullValue()));
    }
}