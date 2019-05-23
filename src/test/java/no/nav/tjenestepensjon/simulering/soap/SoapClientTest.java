package no.nav.tjenestepensjon.simulering.soap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.jupiter.api.Test;
import org.springframework.ws.client.core.WebServiceTemplate;

import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.HentStillingsprosentListeResponse;
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.v1.HentStillingsprosentListe;
import no.nav.tjenestepensjon.simulering.domain.Stillingsprosent;
import no.nav.tjenestepensjon.simulering.domain.TPOrdning;
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor;

class SoapClientTest {

    @Test
    void getStillingsprosenter_shall_return_list() throws Exception {
        var template = mock(WebServiceTemplate.class);
        List<no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.Stillingsprosent> stillingsprosenter = prepareStillingsprosenter();
        when(template.marshalSendAndReceive(any(HentStillingsprosentListe.class), any())).thenReturn(new TestResponse(stillingsprosenter));
        SoapClient client = new SoapClient(template);
        TPOrdning tpOrdning = new TPOrdning("tss1", "tpnr1");
        tpOrdning.setTpLeverandor(new TpLeverandor("name", "url", TpLeverandor.EndpointImpl.SOAP));

        List<Stillingsprosent> result = client.getStillingsprosenter("fnr1", "simulering1", tpOrdning);

        assertStillingsprosenter(result, stillingsprosenter);
    }

    private static void assertStillingsprosenter(List<Stillingsprosent> expected,
            List<no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.Stillingsprosent> actual) {
        assertEquals(expected.size(), actual.size());

        for (int index = 0; index < expected.size(); index++) {
            assertStillingsprosent(expected.get(index), actual.get(index));
        }
    }

    private static void assertStillingsprosent(Stillingsprosent expected, no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.Stillingsprosent actual) {
        assertEquals(expected.getStillingsprosent(), actual.getStillingsprosent());
        assertDate(expected.getDatoFom(), actual.getDatoFom());
        assertDate(expected.getDatoTom(), actual.getDatoTom());
        assertEquals(expected.getFaktiskHovedlonn(), actual.getFaktiskHovedlonn());
        assertEquals(expected.getStillingsuavhengigTilleggslonn(), actual.getStillingsuavhengigTilleggslonn());
        assertEquals(expected.getAldersgrense(), actual.getAldersgrense());
    }

    private static void assertDate(LocalDate expected, XMLGregorianCalendar actual) {
        assertEquals(expected.getYear(), actual.getYear());
        assertEquals(expected.getMonth().getValue(), actual.getMonth());
        assertEquals(expected.getDayOfMonth(), actual.getDay());
    }

    private static List<no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.Stillingsprosent> prepareStillingsprosenter() {
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

    private static class StillingsprosentBuilder {

        private final no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.Stillingsprosent stillingsprosent =
                new no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.Stillingsprosent();

        StillingsprosentBuilder stillingsprosent(double value) {
            stillingsprosent.setStillingsprosent(value);
            return this;
        }

        StillingsprosentBuilder datoFom(int year, int month, int day) {
            XMLGregorianCalendar calendar = mock(XMLGregorianCalendar.class);
            when(calendar.getYear()).thenReturn(year);
            when(calendar.getMonth()).thenReturn(month);
            when(calendar.getDay()).thenReturn(day);
            stillingsprosent.setDatoFom(calendar);
            return this;
        }

        StillingsprosentBuilder datoTom(int year, int month, int day) {
            XMLGregorianCalendar calendar = mock(XMLGregorianCalendar.class);
            when(calendar.getYear()).thenReturn(year);
            when(calendar.getMonth()).thenReturn(month);
            when(calendar.getDay()).thenReturn(day);
            stillingsprosent.setDatoTom(calendar);
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

        no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.Stillingsprosent build() {
            return stillingsprosent;
        }
    }

    private class TestResponse extends HentStillingsprosentListeResponse {

        TestResponse(List<no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.Stillingsprosent> stillingsprosenter) {
            stillingsprosentListe = stillingsprosenter;
        }
    }
}