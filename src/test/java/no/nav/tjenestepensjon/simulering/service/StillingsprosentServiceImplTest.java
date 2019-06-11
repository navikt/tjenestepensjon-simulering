package no.nav.tjenestepensjon.simulering.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static no.nav.tjenestepensjon.simulering.AsyncExecutor.AsyncResponse;
import static no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringMetrics.Metrics.APP_NAME;
import static no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringMetrics.Metrics.APP_TOTAL_STILLINGSPROSENT_CALLS;
import static no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringMetrics.Metrics.APP_TOTAL_STILLINGSPROSENT_TIME;
import static no.nav.tjenestepensjon.simulering.domain.TpLeverandor.EndpointImpl.SOAP;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.tjenestepensjon.simulering.AsyncExecutor;
import no.nav.tjenestepensjon.simulering.TjenestepensjonSimuleringMetrics;
import no.nav.tjenestepensjon.simulering.domain.Stillingsprosent;
import no.nav.tjenestepensjon.simulering.domain.TPOrdning;
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor;
import no.nav.tjenestepensjon.simulering.exceptions.DuplicateStillingsprosentEndDateException;
import no.nav.tjenestepensjon.simulering.exceptions.MissingStillingsprosentException;

@ExtendWith(MockitoExtension.class)
class StillingsprosentServiceImplTest {

    private static final LocalDate jan2019 = LocalDate.of(2019, Month.JANUARY, 1);
    private static final LocalDate feb2019 = LocalDate.of(2019, Month.FEBRUARY, 1);
    private static final LocalDate mar2019 = LocalDate.of(2019, Month.MARCH, 1);
    private static final LocalDate apr2019 = LocalDate.of(2019, Month.APRIL, 1);
    private static final LocalDate may2019 = LocalDate.of(2019, Month.MAY, 1);

    @Mock
    TjenestepensjonSimuleringMetrics metrics;
    @Mock
    AsyncExecutor asyncExecutor;

    @InjectMocks
    StillingsprosentServiceImpl stillingsprosentService;

    @Test
    void shouldRetrieveFromTpRegisterAsync() {
        when(asyncExecutor.executeAsync(any(Map.class))).thenReturn(mock(AsyncResponse.class));
        stillingsprosentService.getStillingsprosentListe("123", Map.of(new TPOrdning("1", "1"), new TpLeverandor("name","url", SOAP)));
        verify(asyncExecutor).executeAsync(any(Map.class));
    }

    @Test
    void handlesMetrics() {
        when(asyncExecutor.executeAsync(any(Map.class))).thenReturn(mock(AsyncResponse.class));
        stillingsprosentService.getStillingsprosentListe("123", Map.of(new TPOrdning("1", "1"), new TpLeverandor("name","url", SOAP)));
        verify(metrics).incrementCounter(eq(APP_NAME), eq(APP_TOTAL_STILLINGSPROSENT_CALLS));
        verify(metrics).incrementCounter(eq(APP_NAME), eq(APP_TOTAL_STILLINGSPROSENT_TIME), any(Double.class));
    }

    @Test
    void getLatestSingleForholdAndStillingsprosent() throws Exception {
        Map<TPOrdning, List<Stillingsprosent>> map = new HashMap<>();
        TPOrdning tpOrdning = new TPOrdning("1", "1");
        List<Stillingsprosent> pcts = List.of(createPct(jan2019, feb2019));
        map.put(tpOrdning, pcts);

        assertThat(stillingsprosentService.getLatestFromStillingsprosent(map), is(tpOrdning));
    }

    @Test
    void getLatestSingleForholdAndThreeStillingsprosent() throws Exception {
        Map<TPOrdning, List<Stillingsprosent>> map = new HashMap<>();
        TPOrdning tpOrdning = new TPOrdning("1", "1");
        List<Stillingsprosent> pcts = List.of(
                createPct(jan2019, feb2019),
                createPct(feb2019, apr2019),
                createPct(jan2019, mar2019)
        );
        map.put(tpOrdning, pcts);

        assertThat(stillingsprosentService.getLatestFromStillingsprosent(map), is(tpOrdning));
    }

    @Test
    void getLatestSingleTwoForholdAndThreeStillingsprosent() throws Exception {
        Map<TPOrdning, List<Stillingsprosent>> map = new HashMap<>();
        TPOrdning tpOrdning = new TPOrdning("1", "1");
        TPOrdning tpOrdning2 = new TPOrdning("2", "2");
        List<Stillingsprosent> pcts1 = List.of(
                createPct(jan2019, feb2019),
                createPct(feb2019, apr2019),
                createPct(jan2019, mar2019)
        );
        map.put(tpOrdning, pcts1);

        List<Stillingsprosent> pcts2 = List.of(
                createPct(jan2019, feb2019),
                createPct(feb2019, may2019)
        );
        map.put(tpOrdning2, pcts2);

        assertThat(stillingsprosentService.getLatestFromStillingsprosent(map), is(tpOrdning2));
    }

    @Test
    void throwsExceptionIfLatestEndDateIsNotUnique() {
        Map<TPOrdning, List<Stillingsprosent>> map = new HashMap<>();
        TPOrdning tpOrdning = new TPOrdning("1", "1");
        TPOrdning tpOrdning2 = new TPOrdning("2", "2");
        List<Stillingsprosent> pcts1 = List.of(
                createPct(jan2019, may2019)
        );
        map.put(tpOrdning, pcts1);

        List<Stillingsprosent> pcts2 = List.of(
                createPct(feb2019, may2019)
        );
        map.put(tpOrdning2, pcts2);

        assertThrows(DuplicateStillingsprosentEndDateException.class, () -> stillingsprosentService.getLatestFromStillingsprosent(map));
    }

    @Test
    void nullIsGreaterThanDate() throws Exception {
        Map<TPOrdning, List<Stillingsprosent>> map = new HashMap<>();
        TPOrdning tpOrdning = new TPOrdning("1", "1");
        TPOrdning tpOrdning2 = new TPOrdning("2", "2");
        TPOrdning tpOrdning3 = new TPOrdning("3", "3");
        List<Stillingsprosent> pcts1 = List.of(
                createPct(jan2019, feb2019),
                createPct(feb2019, apr2019),
                createPct(apr2019, mar2019)
        );
        map.put(tpOrdning, pcts1);

        List<Stillingsprosent> pcts2 = List.of(
                createPct(jan2019, feb2019),
                createPct(feb2019, may2019)
        );
        map.put(tpOrdning2, pcts2);

        List<Stillingsprosent> pcts3 = List.of(
                createPct(jan2019, feb2019),
                createPct(feb2019, null)
        );
        map.put(tpOrdning3, pcts3);

        assertThat(stillingsprosentService.getLatestFromStillingsprosent(map), is(tpOrdning3));
    }

    @Test
    void throwsExceptionWhenNoStillingsprosentIsFound() {
        Map<TPOrdning, List<Stillingsprosent>> map = new HashMap<>();
        TPOrdning tpOrdning = new TPOrdning("1", "1");
        TPOrdning tpOrdning2 = new TPOrdning("2", "2");
        List<Stillingsprosent> pcts1 = Collections.emptyList();
        map.put(tpOrdning, pcts1);

        List<Stillingsprosent> pcts2 = Collections.emptyList();
        map.put(tpOrdning2, pcts2);

        assertThrows(MissingStillingsprosentException.class, () -> stillingsprosentService.getLatestFromStillingsprosent(map));
    }

    private Stillingsprosent createPct(LocalDate fom, LocalDate tom) {
        Stillingsprosent s = new Stillingsprosent();
        s.setDatoFom(fom);
        s.setDatoTom(tom);
        return s;
    }
}