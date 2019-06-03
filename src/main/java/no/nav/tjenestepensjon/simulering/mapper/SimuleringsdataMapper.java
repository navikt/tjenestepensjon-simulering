package no.nav.tjenestepensjon.simulering.mapper;

import static no.nav.tjenestepensjon.simulering.domain.DelytelseType.BASISGP;
import static no.nav.tjenestepensjon.simulering.domain.DelytelseType.BASISPT;
import static no.nav.tjenestepensjon.simulering.domain.DelytelseType.BASISTP;
import static no.nav.tjenestepensjon.simulering.domain.DelytelseType.SKJERMINGSTILLEGG;

import java.util.List;

import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.Simuleringsdata;
import no.nav.tjenestepensjon.simulering.domain.DelytelseType;
import no.nav.tjenestepensjon.simulering.rest.IncomingRequest.Delytelse;
import no.nav.tjenestepensjon.simulering.rest.IncomingRequest.Simuleringsperiode;

public class SimuleringsdataMapper {

    public static Simuleringsdata mapToSimuleringsdata(Simuleringsperiode periode) {
        Simuleringsdata simuleringsdata = new Simuleringsdata();
        simuleringsdata.setPoengArTom1991(periode.getPoengArTom1991());
        simuleringsdata.setPoengArFom1992(periode.getPoengArFom1992());
        simuleringsdata.setSluttpoengtall(periode.getSluttpoengtall());
        simuleringsdata.setAnvendtTrygdetid(periode.getAnvendtTrygdetid());
        simuleringsdata.setBasisgp(getDelytelseBelop(periode.getDelytelser(), BASISGP));
        simuleringsdata.setBasispt(getDelytelseBelop(periode.getDelytelser(), BASISPT));
        simuleringsdata.setBasistp(getDelytelseBelop(periode.getDelytelser(), BASISTP));
        simuleringsdata.setForholdstallUttak(periode.getForholdstall());
        simuleringsdata.setSkjermingstillegg(getDelytelseBelop(periode.getDelytelser(), SKJERMINGSTILLEGG));
        simuleringsdata.setUforegradVedOmregning(periode.getUforegradVedOmregning());
        return simuleringsdata;
    }

    private static Double getDelytelseBelop(List<Delytelse> delytelser, DelytelseType delytelseType) {
        return delytelser.stream()
                .filter(delytelse -> delytelse.hasPensjonstype(delytelseType))
                .findFirst().map(Delytelse::getBelop).orElse(null);
    }
}
