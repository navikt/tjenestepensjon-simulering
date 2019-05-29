package no.nav.tjenestepensjon.simulering.mapper;

import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.ObjectFactory;
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.SimulertAP2011;
import no.nav.tjenestepensjon.simulering.rest.IncomingRequest;

public class SimulertAP2011Mapper {

    public SimulertAP2011 mapToSimulertAP2011(IncomingRequest.Simuleringsperiode simuleringsperiode) {
        if (simuleringsperiode == null)
            return null;

        var simuleringsdata = new ObjectFactory().createSimuleringsdata();
        simuleringsdata.setPoengArFom1992(simuleringsperiode.getPoengArFom1992());
        simuleringsdata.setPoengArTom1991(simuleringsdata.getPoengArTom1991());
        simuleringsdata.setSluttpoengtall(simuleringsdata.getSluttpoengtall());
        simuleringsdata.setAnvendtTrygdetid(simuleringsdata.getAnvendtTrygdetid());
        simuleringsdata.setBasisgp(simuleringsdata.getBasisgp());
        simuleringsdata.setBasistp(simuleringsdata.getBasistp());
        simuleringsdata.setBasispt(simuleringsdata.getBasispt());
        simuleringsdata.setForholdstallUttak(simuleringsdata.getForholdstallUttak());
        simuleringsdata.setSkjermingstillegg(simuleringsdata.getSkjermingstillegg());
        simuleringsdata.setUforegradVedOmregning(simuleringsdata.getUforegradVedOmregning());

        var simulertAP2011 = new ObjectFactory().createSimulertAP2011();
        simulertAP2011.setSimulertForsteuttak(simuleringsdata);

        return simulertAP2011;
    }
}
