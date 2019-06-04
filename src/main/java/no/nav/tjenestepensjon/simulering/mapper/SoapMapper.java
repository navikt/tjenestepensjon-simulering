package no.nav.tjenestepensjon.simulering.mapper;

import static no.nav.tjenestepensjon.simulering.mapper.AFPPrivatMapper.mapToSimulertAFPPrivat;
import static no.nav.tjenestepensjon.simulering.mapper.SimulertAP2011Mapper.mapToSimulertAP2011;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.ObjectFactory;
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.v1.HentStillingsprosentListe;
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.v1.HentStillingsprosentListeResponse;
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.v1.SimulerOffentligTjenestepensjon;
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.v1.SimulerOffentligTjenestepensjonResponse;
import no.nav.tjenestepensjon.simulering.domain.Stillingsprosent;
import no.nav.tjenestepensjon.simulering.domain.TPOrdning;
import no.nav.tjenestepensjon.simulering.rest.IncomingRequest;
import no.nav.tjenestepensjon.simulering.rest.OutgoingResponse;
import no.nav.tjenestepensjon.simulering.rest.OutgoingResponse.SimulertPensjon;

public class SoapMapper {

    private static final no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.v1.ObjectFactory wrapperFactory =
            new no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.v1.ObjectFactory();
    private static final ObjectFactory contentFactory = new ObjectFactory();

    public static HentStillingsprosentListe mapStillingsprosentRequest(String fnr, TPOrdning tpOrdning) {
        var wrapperRequest = wrapperFactory.createHentStillingsprosentListe();
        var request = contentFactory.createHentStillingsprosentListeRequest();
        request.setFnr(fnr);
        request.setTpnr(tpOrdning.getTpId());
        request.setTssEksternId(tpOrdning.getTssId());
        request.setSimuleringsKode("AP");
        wrapperRequest.setRequest(request);
        return wrapperRequest;
    }

    public static List<Stillingsprosent> mapStillingsprosentResponse(HentStillingsprosentListeResponse response) {
        return response.getResponse().getStillingsprosentListe().stream()
                .map(new StillingsprosentMapper()::mapToStillingsprosent)
                .collect(Collectors.toList());
    }

    public static SimulerOffentligTjenestepensjon mapSimulerTjenestepensjonRequest(IncomingRequest incomingRequest, TPOrdning tpOrdning) {
        var wrapperRequest = wrapperFactory.createSimulerOffentligTjenestepensjon();
        var request = contentFactory.createSimulerOffentligTjenestepensjonRequest();
        var simulerTjenestepensjon = contentFactory.createSimulerTjenestepensjon();
        simulerTjenestepensjon.setFnr(incomingRequest.getFnr());
        simulerTjenestepensjon.setTpnr(tpOrdning.getTpId());
        simulerTjenestepensjon.setTssEksternId(tpOrdning.getTssId());
        simulerTjenestepensjon.setSivilstandKode(incomingRequest.getSivilstandkode());
        simulerTjenestepensjon.setSprak(incomingRequest.getSprak());
        simulerTjenestepensjon.setSimulertAFPOffentlig(incomingRequest.getSimulertAFPOffentlig());
        simulerTjenestepensjon.setSimulertAFPPrivat(mapToSimulertAFPPrivat(incomingRequest.getSimulertAFPPrivat()));
//        simulerTjenestepensjon.setForsteUttakDato();
//        simulerTjenestepensjon.setUttaksgrad();
//        simulerTjenestepensjon.setHeltUttakDato();
//        simulerTjenestepensjon.setStillingsprosentOffHeltUttak();
//        simulerTjenestepensjon.setStillingsprosentOffGradertUttak();
//        simulerTjenestepensjon.setInntektForUttak();
//        simulerTjenestepensjon.setInntektUnderGradertUttak();
//        simulerTjenestepensjon.setInntektEtterHeltUttak();
//        simulerTjenestepensjon.setAntallArInntektEtterHeltUttak();
        simulerTjenestepensjon.setSimulertAP2011(mapToSimulertAP2011(incomingRequest.getSimuleringsperioder()));

        request.setSimulerTjenestepensjon(simulerTjenestepensjon);
        wrapperRequest.setRequest(request);

        return wrapperRequest;
    }

    public static List<SimulertPensjon> mapSimulerTjenestepensjonResponse(SimulerOffentligTjenestepensjonResponse response) {
        var simulertPensjonList = new ArrayList<SimulertPensjon>();

        for (var simulertPensjon : response.getResponse().getSimulertPensjonListe()) {
            var mappedSimulertPensjon = new SimulertPensjon();
            mappedSimulertPensjon.setTpnr(simulertPensjon.getTpnr());
            mappedSimulertPensjon.setNavnOrdning(simulertPensjon.getNavnOrdning());
            mappedSimulertPensjon.setInkluderteOrdninger(simulertPensjon.getInkludertOrdningListe());
            mappedSimulertPensjon.setLeverandorUrl(simulertPensjon.getLeverandorUrl());

            var utbetalingsperioder = new ArrayList<OutgoingResponse.Utbetalingsperiode>();
            for (var utbetalingsperiode : simulertPensjon.getUtbetalingsperiodeListe()) {
                var mappedUtbetalingsperiode = new OutgoingResponse.Utbetalingsperiode();
                mappedUtbetalingsperiode.setStartDato(convertToDato(utbetalingsperiode.getStartAlder(), utbetalingsperiode.getStartManed()));
                mappedUtbetalingsperiode.setSluttDato(convertToDato(utbetalingsperiode.getSluttAlder(), utbetalingsperiode.getSluttManed()));
                mappedUtbetalingsperiode.setGrad(utbetalingsperiode.getGrad());
                mappedUtbetalingsperiode.setArligUtbetaling(utbetalingsperiode.getArligUtbetaling());
                mappedUtbetalingsperiode.setYtelsekode(utbetalingsperiode.getYtelseKode());
                mappedUtbetalingsperiode.setMangelfullSimuleringkode(utbetalingsperiode.getMangelfullSimuleringKode());
            }
            mappedSimulertPensjon.setUtbetalingsperioder(null);
            simulertPensjonList.add(mappedSimulertPensjon);
        }
        return simulertPensjonList;
    }

    //TODO: Date converter
    private static Date convertToDato(Integer startAlder, Integer startManed) {
        return null;
    }
}
