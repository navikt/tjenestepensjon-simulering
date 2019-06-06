package no.nav.tjenestepensjon.simulering.mapper;

import no.nav.tjenestepensjon.simulering.domain.Stillingsprosent;
import no.nav.tjenestepensjon.simulering.mapper.StillingsprosentMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StillingsprosentMappingTests {

    private StillingsprosentMapper mapper = new StillingsprosentMapper();

    private static XMLGregorianCalendar gregorianDate;
    private static LocalDate localDate;

    @BeforeAll
    public static void setup() throws Exception {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(new Date(100L * 1000L * 60L * 60L * 24L));
        gregorianDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
        localDate = LocalDate.ofEpochDay(100);
    }

    @Test
    public void stillingsprosent_v1_maps_to_domain() {
        no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.Stillingsprosent original =
            new no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.Stillingsprosent();
        original.setStillingsprosent(100.0);
        original.setDatoFom(gregorianDate);
        original.setDatoTom(gregorianDate);
        original.setFaktiskHovedlonn("0");
        original.setStillingsuavhengigTilleggslonn("100");
        original.setAldersgrense(0);

        var expected = new Stillingsprosent();
        expected.setStillingsprosent(100.0);
        expected.setDatoFom(localDate);
        expected.setDatoTom(localDate);
        expected.setFaktiskHovedlonn("0");
        expected.setStillingsuavhengigTilleggslonn("100");
        expected.setAldersgrense(0);

        var actual = mapper.mapToStillingsprosent(original);

        assertEquals(expected.getStillingsprosent(), actual.getStillingsprosent());
        assertEquals(expected.getDatoFom(), actual.getDatoFom());
        assertEquals(expected.getDatoTom(), actual.getDatoTom());
        assertEquals(expected.getFaktiskHovedlonn(), actual.getFaktiskHovedlonn());
        assertEquals(expected.getStillingsuavhengigTilleggslonn(), actual.getStillingsuavhengigTilleggslonn());
        assertEquals(expected.getAldersgrense(), actual.getAldersgrense());

    }
}
