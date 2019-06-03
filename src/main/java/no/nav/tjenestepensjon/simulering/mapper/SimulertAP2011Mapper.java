package no.nav.tjenestepensjon.simulering.mapper;

import static no.nav.tjenestepensjon.simulering.mapper.SimuleringsdataMapper.mapToSimuleringsdata;

import java.util.List;

import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.SimulertAP2011;
import no.nav.tjenestepensjon.simulering.rest.IncomingRequest.Simuleringsperiode;

public class SimulertAP2011Mapper {

    public static SimulertAP2011 mapToSimulertAP2011(List<Simuleringsperiode> simuleringsperiodeList) {
        SimulertAP2011 simulertAP2011 = new SimulertAP2011();
        if (simuleringsperiodeList.size() == 1) {
            simulertAP2011.setSimulertForsteuttak(mapToSimuleringsdata(simuleringsperiodeList.get(0)));
        } else {
            //Will have two periods if gradert førsteuttak
            simulertAP2011.setSimulertForsteuttak(mapToSimuleringsdata(simuleringsperiodeList.stream().min(SimulertAP2011Mapper::order).get()));
            simulertAP2011.setSimulertHeltUttakEtter67Ar(mapToSimuleringsdata(simuleringsperiodeList.stream().max(SimulertAP2011Mapper::order).get()));
        }
        return simulertAP2011;
    }

    private static int order(Simuleringsperiode o1, Simuleringsperiode o2) {
        if (o1.getDatoFom().before(o2.getDatoFom())) {
            return -1;
        } else if (o1.getDatoFom().after(o2.getDatoFom())) {
            return 1;
        } else {
            return 0;
        }
    }
}
