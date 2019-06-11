package no.nav.tjenestepensjon.simulering;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static no.nav.tjenestepensjon.simulering.domain.TpLeverandor.EndpointImpl.REST;
import static no.nav.tjenestepensjon.simulering.domain.TpLeverandor.EndpointImpl.SOAP;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ws.client.WebServiceIOException;

import no.nav.tjenestepensjon.simulering.domain.Stillingsprosent;
import no.nav.tjenestepensjon.simulering.domain.TPOrdning;
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor;
import no.nav.tjenestepensjon.simulering.exceptions.StillingsprosentCallableException;

@ExtendWith(MockitoExtension.class)
class StillingsprosentCallableTest {

    @Mock
    private TjenestepensjonsimuleringEndpointRouter simuleringEndPointRouter;
    @Mock
    private TjenestepensjonSimuleringMetrics metrics;

    @Test
    void call_shall_return_stillingsprosenter_with_soap() throws Exception {
        var tpOrdning = new TPOrdning("tss1", "tp1");
        var tpLeverandor = new TpLeverandor("lev", "url1", SOAP);
        var callable = new StillingsprosentCallable("fnr1", tpOrdning, tpLeverandor, simuleringEndPointRouter, metrics);
        List<Stillingsprosent> stillingsprosenter = prepareStillingsprosenter();
        when(simuleringEndPointRouter.getStillingsprosenter(any(), any(), any())).thenReturn(stillingsprosenter);

        List<Stillingsprosent> result = callable.call();

        verify(metrics).incrementCounter(eq(tpLeverandor.getName()), any());
        assertStillingsprosenter(stillingsprosenter, result);
    }

    @Test
    void call_shall_return_stillingsprosenter_with_rest() throws Exception {
        var tpOrdning = new TPOrdning("tss1", "tp1");
        var tpLeverandor = new TpLeverandor("lev", "url1", REST);
        var callable = new StillingsprosentCallable("fnr1", tpOrdning, tpLeverandor, simuleringEndPointRouter, metrics);
        List<Stillingsprosent> stillingsprosenter = prepareStillingsprosenter();
        when(simuleringEndPointRouter.getStillingsprosenter(any(), any(), any())).thenReturn(stillingsprosenter);

        List<Stillingsprosent> result = callable.call();

        verify(metrics).incrementCounter(eq(tpLeverandor.getName()), any());
        assertStillingsprosenter(stillingsprosenter, result);
    }

    @Test
    void exception_shall_be_rethrown_as_StillingsprosentCallableException_with_soap() throws Exception {
        var tpOrdning = new TPOrdning("tss1", "tp1");
        var tpLeverandor = new TpLeverandor("lev", "url1", SOAP);
        var callable = new StillingsprosentCallable("fnr1", tpOrdning, tpLeverandor, simuleringEndPointRouter, metrics);
        when(simuleringEndPointRouter.getStillingsprosenter(any(), any(), any())).thenThrow(new WebServiceIOException("msg from cause"));

        StillingsprosentCallableException exception = assertThrows(StillingsprosentCallableException.class, () -> callable.call());
        assertThat(exception.getMessage(), is("Call to getStillingsprosenter failed: msg from cause"));
        assertThat(exception.getTpOrdning(), is(tpOrdning));
    }

    @Test
    void exception_shall_be_rethrown_as_StillingsprosentCallableException_with_rest() throws Exception {
        var tpOrdning = new TPOrdning("tss1", "tp1");
        var tpLeverandor = new TpLeverandor("lev", "url1", REST);
        var callable = new StillingsprosentCallable("fnr1", tpOrdning, tpLeverandor, simuleringEndPointRouter, metrics);
        when(simuleringEndPointRouter.getStillingsprosenter(any(), any(), any())).thenThrow(new WebServiceIOException("msg from cause"));

        StillingsprosentCallableException exception = assertThrows(StillingsprosentCallableException.class, () -> callable.call());
        assertThat(exception.getMessage(), is("Call to getStillingsprosenter failed: msg from cause"));
        assertThat(exception.getTpOrdning(), is(tpOrdning));
    }

    private static List<Stillingsprosent> prepareStillingsprosenter() {
        var stillingsprosent1 = new StillingsprosentBuilder()
                .stillingsprosent(100d)
                .aldersgrense(70)
                .datoFom(2018, 1, 2)
                .datoTom(2029, 12, 31)
                .faktiskHovedlonn("hovedlønn1")
                .stillingsuavhengigTilleggslonn("tilleggslønn1")
                .build();

        var stillingsprosent2 = new StillingsprosentBuilder()
                .stillingsprosent(12.5d)
                .aldersgrense(67)
                .datoFom(2019, 2, 3)
                .datoTom(2035, 11, 30)
                .faktiskHovedlonn("hovedlønn2")
                .stillingsuavhengigTilleggslonn("tilleggslønn2")
                .build();

        return Arrays.asList(stillingsprosent1, stillingsprosent2);
    }

    private static void assertStillingsprosenter(List<Stillingsprosent> expected, List<Stillingsprosent> actual) {
        assertEquals(expected.size(), actual.size());

        for (int index = 0; index < expected.size(); index++) {
            assertStillingsprosent(expected.get(index), actual.get(index));
        }
    }

    private static void assertStillingsprosent(Stillingsprosent expected, Stillingsprosent actual) {
        assertEquals(expected.getStillingsprosent(), actual.getStillingsprosent());
        assertEquals(expected.getDatoFom(), actual.getDatoFom());
        assertEquals(expected.getDatoTom(), actual.getDatoTom());
        assertEquals(expected.getFaktiskHovedlonn(), actual.getFaktiskHovedlonn());
        assertEquals(expected.getStillingsuavhengigTilleggslonn(), actual.getStillingsuavhengigTilleggslonn());
        assertEquals(expected.getAldersgrense(), actual.getAldersgrense());
    }

    private static class StillingsprosentBuilder {

        private final Stillingsprosent stillingsprosent = new Stillingsprosent();

        StillingsprosentBuilder stillingsprosent(double value) {
            stillingsprosent.setStillingsprosent(value);
            return this;
        }

        StillingsprosentBuilder datoFom(int year, int month, int day) {
            stillingsprosent.setDatoFom(LocalDate.of(year, month, day));
            return this;
        }

        StillingsprosentBuilder datoTom(int year, int month, int day) {
            stillingsprosent.setDatoTom(LocalDate.of(year, month, day));
            return this;
        }

        StillingsprosentBuilder faktiskHovedlonn(String value) {
            stillingsprosent.setFaktiskHovedlonn(value);
            return this;
        }

        StillingsprosentBuilder stillingsuavhengigTilleggslonn(String value) {
            stillingsprosent.setStillingsuavhengigTilleggslonn(value);
            return this;
        }

        StillingsprosentBuilder aldersgrense(int value) {
            stillingsprosent.setAldersgrense(value);
            return this;
        }

        Stillingsprosent build() {
            return stillingsprosent;
        }
    }
}
