package no.nav.tjenestepensjon.simulering.mapper;

import static no.nav.tjenestepensjon.simulering.mapper.SimuleringsdataMapper.mapToSimuleringsdata;
import static no.nav.tjenestepensjon.simulering.util.Utils.createDayResolutionCalendar;
import static no.nav.tjenestepensjon.simulering.util.Utils.getBirthDate;
import static no.nav.tjenestepensjon.simulering.util.Utils.isSameDay;

import java.util.Calendar;
import java.util.Date;

import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.SimulertAP2011;
import no.nav.tjenestepensjon.simulering.rest.IncomingRequest.Simuleringsperiode;

public class SimulertAP2011Mapper {

    /**
     * Mapping function for scenario fullt uttak (forsteUttak == 100%).
     * If the user is older than 67 at the time of fullt uttak, the values from the forsteUttak is also set on {@link SimulertAP2011#getSimulertHeltUttakEtter67Ar()}.
     * Prior to the transition to the new kap.20 information-model, PEN would provide this value to the ESB, but this information is "lost" in the new model.
     *
     * @param forsteUttak {@link Simuleringsperiode} for forsteuttak
     * @param fnr fodselsnr
     * @return {@link SimulertAP2011}
     * @see "TPEN630 SimulerTP and FPEN032 hentBeregningsinformasjonForTP in PEN for further reference"
     */
    public static SimulertAP2011 mapFulltUttak(Simuleringsperiode forsteUttak, String fnr) {
        SimulertAP2011 simulertAP2011 = new SimulertAP2011();
        simulertAP2011.setSimulertForsteuttak(mapToSimuleringsdata(forsteUttak));
        if (isUttakEtter67Ar(forsteUttak, fnr)) {
            simulertAP2011.setSimulertHeltUttakEtter67Ar(mapToSimuleringsdata(forsteUttak));
        }
        return simulertAP2011;
    }

    /**
     * Mapping function for scenario gradert uttak (forstUttak < 100%)
     * Unlike the scenario for fullt uttak, PEN will provide two periods for gradert uttak. In this case, the value of {@link SimulertAP2011#getSimulertHeltUttakEtter67Ar()}
     * should be set without checking the age (this is handled by PEN).
     *
     * @param forsteUttak {@link Simuleringsperiode} for forsteuttak
     * @param heltUttak {@link Simuleringsperiode} for helt uttak
     * @return {@link SimulertAP2011}
     * @see "TPEN630 SimulerTP and FPEN032 hentBeregningsinformasjonForTP in PEN for further reference"
     */
    public static SimulertAP2011 mapGradertUttak(Simuleringsperiode forsteUttak, Simuleringsperiode heltUttak) {
        SimulertAP2011 simulertAP2011 = new SimulertAP2011();
        simulertAP2011.setSimulertForsteuttak(mapToSimuleringsdata(forsteUttak));
        simulertAP2011.setSimulertHeltUttakEtter67Ar(mapToSimuleringsdata(heltUttak));
        return simulertAP2011;
    }

    private static boolean isUttakEtter67Ar(Simuleringsperiode simuleringsperiode, String fnr) {
        Date dateAt67Years = findDateAt67Years(fnr);
        return isSameDay(simuleringsperiode.getDatoFom(), dateAt67Years) || simuleringsperiode.getDatoFom().after(dateAt67Years);
    }

    private static Date findDateAt67Years(String fnr) {
        Calendar calendar = createDayResolutionCalendar(getBirthDate(fnr));
        calendar.add(Calendar.YEAR, 67);
        return calendar.getTime();
    }
}
