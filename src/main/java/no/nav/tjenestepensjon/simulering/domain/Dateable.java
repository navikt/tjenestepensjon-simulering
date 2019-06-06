package no.nav.tjenestepensjon.simulering.domain;

import java.util.Date;

public interface Dateable {
    Date getDatoFom();

    static int sortAscendingByFomDato(Dateable o1, Dateable o2) {
        if (o1.getDatoFom().before(o2.getDatoFom())) {
            return -1;
        } else if (o1.getDatoFom().after(o2.getDatoFom())) {
            return 1;
        } else {
            return 0;
        }
    }
}
