package no.nav.tjenestepensjon.simulering.mapper;

import static no.nav.tjenestepensjon.simulering.util.Utils.convertToXmlGregorianCalendar;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import no.nav.tjenestepensjon.simulering.domain.Stillingsprosent;

// Maps WSDL generated Stillingsprosent class to domain class
public class StillingsprosentMapper {

    public static Stillingsprosent mapToStillingsprosent(
            no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.Stillingsprosent stillingsprosent) {
        if (stillingsprosent == null) {
            return null;
        }

        var datoFom = stillingsprosent.getDatoFom();
        var datoTom = stillingsprosent.getDatoTom();

        var mappedStillingsprosent = new Stillingsprosent();
        mappedStillingsprosent.setStillingsprosent(stillingsprosent.getStillingsprosent());
        mappedStillingsprosent.setDatoFom(LocalDate.of(datoFom.getYear(), datoFom.getMonth(), datoFom.getDay()));
        mappedStillingsprosent.setDatoTom(datoTom != null ? LocalDate.of(datoTom.getYear(), datoTom.getMonth(), datoTom.getDay()) : null);
        mappedStillingsprosent.setFaktiskHovedlonn(stillingsprosent.getFaktiskHovedlonn());
        mappedStillingsprosent.setStillingsuavhengigTilleggslonn(stillingsprosent.getStillingsuavhengigTilleggslonn());
        mappedStillingsprosent.setAldersgrense(stillingsprosent.getAldersgrense());

        return mappedStillingsprosent;
    }

    public static no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.Stillingsprosent mapToStillingsprosent(Stillingsprosent stillingsprosent) {
        no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.Stillingsprosent mapped =
                new no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.Stillingsprosent();
        mapped.setDatoFom(convertToXmlGregorianCalendar(Date.from(stillingsprosent.getDatoFom().atStartOfDay(ZoneId.systemDefault()).toInstant())));
        mapped.setDatoTom(stillingsprosent.getDatoTom() != null ?
                convertToXmlGregorianCalendar(Date.from(stillingsprosent.getDatoTom().atStartOfDay(ZoneId.systemDefault()).toInstant())) : null);
        mapped.setAldersgrense(stillingsprosent.getAldersgrense());
        mapped.setFaktiskHovedlonn(stillingsprosent.getFaktiskHovedlonn());
        mapped.setStillingsprosent(stillingsprosent.getStillingsprosent());
        mapped.setStillingsuavhengigTilleggslonn(stillingsprosent.getStillingsuavhengigTilleggslonn());
        return mapped;
    }
}
