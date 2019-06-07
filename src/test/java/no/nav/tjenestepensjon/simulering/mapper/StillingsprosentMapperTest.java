package no.nav.tjenestepensjon.simulering.mapper;

import static java.time.LocalDate.of;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static no.nav.tjenestepensjon.simulering.mapper.StillingsprosentMapper.mapToStillingsprosent;
import static no.nav.tjenestepensjon.simulering.util.Utils.convertToXmlGregorianCalendar;
import static no.nav.tjenestepensjon.simulering.util.Utils.createDate;

import java.time.Month;
import java.util.Calendar;

import org.junit.jupiter.api.Test;

import no.nav.tjenestepensjon.simulering.domain.Stillingsprosent;

class StillingsprosentMapperTest {

    @Test
    public void stillingsprosent_v1_maps_to_domain() {
        no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.Stillingsprosent original =
                new no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.Stillingsprosent();
        original.setStillingsprosent(100.0);
        original.setDatoFom(convertToXmlGregorianCalendar(createDate(2015, Calendar.FEBRUARY, 14)));
        original.setDatoTom(convertToXmlGregorianCalendar(createDate(2015, Calendar.MARCH, 14)));
        original.setFaktiskHovedlonn("0");
        original.setStillingsuavhengigTilleggslonn("100");
        original.setAldersgrense(0);

        var expected = new Stillingsprosent();
        expected.setStillingsprosent(100.0);
        expected.setDatoFom(of(2015, Month.FEBRUARY, 14));
        expected.setDatoTom(of(2015, Month.MARCH, 14));
        expected.setFaktiskHovedlonn("0");
        expected.setStillingsuavhengigTilleggslonn("100");
        expected.setAldersgrense(0);

        var actual = mapToStillingsprosent(original);

        assertEquals(expected.getStillingsprosent(), actual.getStillingsprosent());
        assertEquals(expected.getDatoFom(), actual.getDatoFom());
        assertEquals(expected.getDatoTom(), actual.getDatoTom());
        assertEquals(expected.getFaktiskHovedlonn(), actual.getFaktiskHovedlonn());
        assertEquals(expected.getStillingsuavhengigTilleggslonn(), actual.getStillingsuavhengigTilleggslonn());
        assertEquals(expected.getAldersgrense(), actual.getAldersgrense());
    }

    @Test
    public void domainMapsToStillingsprosentv1() {
        Stillingsprosent original = new Stillingsprosent();
        original.setStillingsprosent(100.0);
        original.setDatoFom(of(2015, Month.FEBRUARY, 14));
        original.setDatoTom(of(2015, Month.MARCH, 14));
        original.setFaktiskHovedlonn("0");
        original.setStillingsuavhengigTilleggslonn("100");
        original.setAldersgrense(0);

        var expected = new no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.Stillingsprosent();
        expected.setStillingsprosent(100.0);
        expected.setDatoFom(convertToXmlGregorianCalendar(createDate(2015, Calendar.FEBRUARY, 14)));
        expected.setDatoTom(convertToXmlGregorianCalendar(createDate(2015, Calendar.MARCH, 14)));
        expected.setFaktiskHovedlonn("0");
        expected.setStillingsuavhengigTilleggslonn("100");
        expected.setAldersgrense(0);

        var actual = mapToStillingsprosent(original);

        assertEquals(expected.getStillingsprosent(), actual.getStillingsprosent());
        assertEquals(expected.getDatoFom(), actual.getDatoFom());
        assertEquals(expected.getDatoTom(), actual.getDatoTom());
        assertEquals(expected.getFaktiskHovedlonn(), actual.getFaktiskHovedlonn());
        assertEquals(expected.getStillingsuavhengigTilleggslonn(), actual.getStillingsuavhengigTilleggslonn());
        assertEquals(expected.getAldersgrense(), actual.getAldersgrense());
    }
}