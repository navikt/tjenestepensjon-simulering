package no.nav.tjenestepensjon.simulering.mapper;

import static java.util.Calendar.YEAR;

import static no.nav.tjenestepensjon.simulering.mapper.AFPPrivatMapper.mapToSimulertAFPPrivat;
import static no.nav.tjenestepensjon.simulering.mapper.SimulertAP2011Mapper.mapFulltUttak;
import static no.nav.tjenestepensjon.simulering.mapper.SimulertAP2011Mapper.mapGradertUttak;
import static no.nav.tjenestepensjon.simulering.mapper.TpForholdMapper.mapToTpForhold;
import static no.nav.tjenestepensjon.simulering.util.Utils.convertToDato;
import static no.nav.tjenestepensjon.simulering.util.Utils.convertToXmlGregorianCalendar;
import static no.nav.tjenestepensjon.simulering.util.Utils.isSameDay;
import static no.nav.tjenestepensjon.simulering.util.Utils.reflectionToString;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.HentStillingsprosentListeRequest;
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.SimulerOffentligTjenestepensjonRequest;
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.SimulerTjenestepensjon;
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.v1.HentStillingsprosentListe;
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.v1.HentStillingsprosentListeResponse;
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.v1.SimulerOffentligTjenestepensjon;
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.v1.SimulerOffentligTjenestepensjonResponse;
import no.nav.tjenestepensjon.simulering.domain.Dateable;
import no.nav.tjenestepensjon.simulering.domain.Stillingsprosent;
import no.nav.tjenestepensjon.simulering.domain.TPOrdning;
import no.nav.tjenestepensjon.simulering.rest.IncomingRequest;
import no.nav.tjenestepensjon.simulering.rest.IncomingRequest.Inntekt;
import no.nav.tjenestepensjon.simulering.rest.IncomingRequest.Simuleringsperiode;
import no.nav.tjenestepensjon.simulering.rest.OutgoingResponse;
import no.nav.tjenestepensjon.simulering.rest.OutgoingResponse.SimulertPensjon;

public class SoapMapper {

    public static final Logger LOG = LoggerFactory.getLogger(SoapMapper.class);

    public static HentStillingsprosentListe mapStillingsprosentRequest(String fnr, TPOrdning tpOrdning) {
        HentStillingsprosentListe wrapperRequest = new HentStillingsprosentListe();
        HentStillingsprosentListeRequest request = new HentStillingsprosentListeRequest();
        request.setFnr(fnr);
        request.setTpnr(tpOrdning.getTpId());
        request.setTssEksternId(tpOrdning.getTssId());
        request.setSimuleringsKode("AP");
        wrapperRequest.setRequest(request);
        return wrapperRequest;
    }

    public static List<Stillingsprosent> mapStillingsprosentResponse(HentStillingsprosentListeResponse response) {
        return response.getResponse().getStillingsprosentListe().stream()
                .map(StillingsprosentMapper::mapToStillingsprosent)
                .collect(Collectors.toList());
    }

    public static SimulerOffentligTjenestepensjon mapSimulerTjenestepensjonRequest(IncomingRequest incomingRequest, TPOrdning tpOrdning,
            Map<TPOrdning, List<Stillingsprosent>> tpOrdningStillingsprosentMap) {
        SimulerOffentligTjenestepensjon wrapperRequest = new SimulerOffentligTjenestepensjon();
        SimulerOffentligTjenestepensjonRequest request = new SimulerOffentligTjenestepensjonRequest();
        SimulerTjenestepensjon simulerTjenestepensjon = new SimulerTjenestepensjon();
        simulerTjenestepensjon.setFnr(incomingRequest.getFnr());
        simulerTjenestepensjon.setTpnr(tpOrdning.getTpId());
        simulerTjenestepensjon.setTssEksternId(tpOrdning.getTssId());
        simulerTjenestepensjon.setSivilstandKode(incomingRequest.getSivilstandkode());
        simulerTjenestepensjon.setSprak(incomingRequest.getSprak());
        simulerTjenestepensjon.setSimulertAFPOffentlig(incomingRequest.getSimulertAFPOffentlig());
        simulerTjenestepensjon.setSimulertAFPPrivat(mapToSimulertAFPPrivat(incomingRequest.getSimulertAFPPrivat()));
        simulerTjenestepensjon.getTpForholdListe().addAll(mapToTpForhold(tpOrdningStillingsprosentMap));

        Simuleringsperiode forsteUttak = incomingRequest.getSimuleringsperioder().stream().min(Dateable::sortAscendingByFomDato).get();
        Optional<Simuleringsperiode> potentialHeltUttak = incomingRequest.getSimuleringsperioder().stream()
                .filter(simuleringsperiode -> !isSameDay(simuleringsperiode.getDatoFom(), forsteUttak.getDatoFom()))
                .max(Dateable::sortAscendingByFomDato);

        if (isGradertForsteuttak(forsteUttak) && potentialHeltUttak.isPresent()) {
            Simuleringsperiode heltUttak = potentialHeltUttak.get();
            simulerTjenestepensjon.setSimulertAP2011(mapGradertUttak(forsteUttak, heltUttak));
            simulerTjenestepensjon.setForsteUttakDato(convertToXmlGregorianCalendar(forsteUttak.getDatoFom()));
            simulerTjenestepensjon.setHeltUttakDato(convertToXmlGregorianCalendar(heltUttak.getDatoFom()));
            simulerTjenestepensjon.setStillingsprosentOffHeltUttak(heltUttak.getStillingsprosentOffentlig());
            simulerTjenestepensjon.setStillingsprosentOffGradertUttak(forsteUttak.getStillingsprosentOffentlig());
            simulerTjenestepensjon.setUttaksgrad(forsteUttak.getUtg());
            simulerTjenestepensjon.setInntektForUttak(findInntektForUttak(incomingRequest.getInntekter(), forsteUttak.getDatoFom()));
            simulerTjenestepensjon.setInntektUnderGradertUttak(findInntektSumOnDate(incomingRequest.getInntekter(), forsteUttak.getDatoFom()));
            simulerTjenestepensjon.setInntektEtterHeltUttak(findInntektSumOnDate(incomingRequest.getInntekter(), heltUttak.getDatoFom()));
            simulerTjenestepensjon.setAntallArInntektEtterHeltUttak(findAntallArInntektEtterHeltUttak(incomingRequest.getInntekter(), heltUttak.getDatoFom()));
        } else {
            simulerTjenestepensjon.setSimulertAP2011(mapFulltUttak(forsteUttak, incomingRequest.getFnr()));
            simulerTjenestepensjon.setForsteUttakDato(convertToXmlGregorianCalendar(forsteUttak.getDatoFom()));
            simulerTjenestepensjon.setStillingsprosentOffHeltUttak(forsteUttak.getStillingsprosentOffentlig());
            simulerTjenestepensjon.setUttaksgrad(forsteUttak.getUtg());
            simulerTjenestepensjon.setInntektForUttak(findInntektForUttak(incomingRequest.getInntekter(), forsteUttak.getDatoFom()));
            simulerTjenestepensjon.setInntektEtterHeltUttak(findInntektSumOnDate(incomingRequest.getInntekter(), forsteUttak.getDatoFom()));
            simulerTjenestepensjon.setAntallArInntektEtterHeltUttak(findAntallArInntektEtterHeltUttak(incomingRequest.getInntekter(), forsteUttak.getDatoFom()));
        }

        request.setSimulerTjenestepensjon(simulerTjenestepensjon);
        wrapperRequest.setRequest(request);
        LOG.info("Mapped IncomingRequest: {} to SimulerOffentligTjenestepensjon: {}", incomingRequest, reflectionToString(simulerTjenestepensjon));
        return wrapperRequest;
    }

    public static List<SimulertPensjon> mapSimulerTjenestepensjonResponse(String fnr, SimulerOffentligTjenestepensjonResponse response) {
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
                mappedUtbetalingsperiode.setDatoFom(convertToDato(fnr, utbetalingsperiode.getStartAlder(), utbetalingsperiode.getStartManed()));
                mappedUtbetalingsperiode.setDatoTom(convertToDato(fnr, utbetalingsperiode.getSluttAlder(), utbetalingsperiode.getSluttManed()));
                mappedUtbetalingsperiode.setGrad(utbetalingsperiode.getGrad());
                mappedUtbetalingsperiode.setArligUtbetaling(utbetalingsperiode.getArligUtbetaling());
                mappedUtbetalingsperiode.setYtelsekode(utbetalingsperiode.getYtelseKode());
                mappedUtbetalingsperiode.setMangelfullSimuleringkode(utbetalingsperiode.getMangelfullSimuleringKode());
                utbetalingsperioder.add(mappedUtbetalingsperiode);
            }
            mappedSimulertPensjon.setUtbetalingsperioder(utbetalingsperioder);
            simulertPensjonList.add(mappedSimulertPensjon);
        }
        LOG.info("Mapped SimulerOffentligTjenestepensjonResponse: {} to List<SimulertPensjon> {}: ", reflectionToString(response.getResponse()), simulertPensjonList.toString());
        return simulertPensjonList;
    }

    /**
     * If forsteUttak is "gradert", there should be two {@link Simuleringsperiode} provided. One period represents the forsteUttak (utg < 100%)
     * and the second represents the heltUttak (utg == 100%).
     * In the case of forsteUttak <em>NOT</em> being "gradert" (utg == 100%), there should only be one
     * period present. In such cases, the values of the period should be mapped to the corresponding fields for "heltUttak".
     *
     * @param forsteUttak {@link Simuleringsperiode} for forsteUttak
     * @return true if uttaksgrad < 100%
     */
    private static boolean isGradertForsteuttak(Simuleringsperiode forsteUttak) {
        return forsteUttak.getUtg() < 100;
    }

    static Integer findInntektSumOnDate(List<Inntekt> inntektList, Date date) {
        return findInntektOnDate(inntektList, date).map(inntekt -> inntekt.getInntekt().intValue()).orElse(null);
    }

    static Optional<Inntekt> findInntektOnDate(List<Inntekt> inntektList, Date date) {
        return inntektList.stream().filter(inntekt -> inntekt.getDatoFom().compareTo(date) == 0).findFirst();
    }

    static Integer findAntallArInntektEtterHeltUttak(List<Inntekt> inntektList, Date uttaksDato) {
        Optional<Inntekt> inntektAtUttaksDato = findInntektOnDate(inntektList, uttaksDato);
        Optional<Inntekt> latestInntekt = inntektList.stream().max(Dateable::sortAscendingByFomDato);
        if (inntektAtUttaksDato.isPresent() && latestInntekt.isPresent()) {
            Calendar inntektAtUttakCalendar = Calendar.getInstance();
            inntektAtUttakCalendar.setTime(inntektAtUttaksDato.get().getDatoFom());
            Calendar latestInntektCalendar = Calendar.getInstance();
            latestInntektCalendar.setTime(latestInntekt.get().getDatoFom());
            return latestInntektCalendar.get(YEAR) - inntektAtUttakCalendar.get(YEAR);
        }
        return 0;
    }

    static Integer findInntektForUttak(List<Inntekt> inntektList, Date uttaksDato) {
        Optional<Inntekt> inntektAtUttaksdato = findInntektOnDate(inntektList, uttaksDato);
        if (inntektAtUttaksdato.isPresent()) {
            List<Inntekt> sorted = inntektList.stream().sorted(Dateable::sortAscendingByFomDato).collect(Collectors.toList());
            Collections.reverse(sorted);
            Iterator<Inntekt> iterator = sorted.iterator();
            boolean removedLaterThanAndIncludingUttaksdato = false;
            while (iterator.hasNext() && !removedLaterThanAndIncludingUttaksdato) {
                Inntekt inntekt = iterator.next();
                if (inntekt.equals(inntektAtUttaksdato.get())) {
                    iterator.remove();
                    removedLaterThanAndIncludingUttaksdato = true;
                } else {
                    iterator.remove();
                }
            }
            return iterator.hasNext() ? iterator.next().getInntekt().intValue() : null;
        }
        return null;
    }
}
