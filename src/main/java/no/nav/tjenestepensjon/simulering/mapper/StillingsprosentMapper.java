package no.nav.tjenestepensjon.simulering.mapper;

import no.nav.tjenestepensjon.simulering.domain.Stillingsprosent;

import java.time.LocalDate;

// Maps WSDL generated Stillingsprosent class to domain class
public class StillingsprosentMapper {

    public Stillingsprosent mapToStillingsprosent(
        no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.Stillingsprosent stillingsprosent) {
        if (stillingsprosent == null)
            return null;

        var datoFom = stillingsprosent.getDatoFom();
        var datoTom = stillingsprosent.getDatoTom();

        var mappedStillingsprosent = new Stillingsprosent();
        mappedStillingsprosent.setStillingsprosent(stillingsprosent.getStillingsprosent());
        mappedStillingsprosent.setDatoFom(LocalDate.of(datoFom.getYear(), datoFom.getMonth(), datoFom.getDay()));
        mappedStillingsprosent.setDatoTom(LocalDate.of(datoTom.getYear(), datoTom.getMonth(), datoTom.getDay()));
        mappedStillingsprosent.setFaktiskHovedlonn(stillingsprosent.getFaktiskHovedlonn());
        mappedStillingsprosent.setStillingsuavhengigTilleggslonn(stillingsprosent.getStillingsuavhengigTilleggslonn());
        mappedStillingsprosent.setAldersgrense(stillingsprosent.getAldersgrense());

        return mappedStillingsprosent;
    }
}
