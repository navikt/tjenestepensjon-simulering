package no.nav.tjenestepensjon.simulering.mapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import static no.nav.tjenestepensjon.simulering.mapper.SimulertAP2011Mapper.mapFulltUttak;
import static no.nav.tjenestepensjon.simulering.mapper.SimulertAP2011Mapper.mapGradertUttak;
import static no.nav.tjenestepensjon.simulering.rest.IncomingRequest.Simuleringsperiode;
import static no.nav.tjenestepensjon.simulering.util.Utils.createDate;

import java.util.Calendar;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.SimulertAP2011;
import no.nav.tjenestepensjon.simulering.rest.IncomingRequest.Delytelse;

class SimulertAP2011MapperTest {

    private static Simuleringsperiode forsteUttak;
    private static Simuleringsperiode heltUttak;
    //Turns 67 in 2027
    private static final String fnr = "01016092500";

    @BeforeEach
    void beforeAll() {
        forsteUttak = new Simuleringsperiode();
        forsteUttak.setPoengArTom1991(15);
        forsteUttak.setPoengArFom1992(17);
        forsteUttak.setSluttpoengtall(5.5d);
        forsteUttak.setAnvendtTrygdetid(64);
        forsteUttak.setForholdstall(100.0);
        forsteUttak.setUforegradVedOmregning(10);
        forsteUttak.setDelytelser(List.of(
                new Delytelse("basisgp", 25000d),
                new Delytelse("basispt", 5000d),
                new Delytelse("basistp", 15000d),
                new Delytelse("skjermingstillegg", 7000d)
        ));

        heltUttak = new Simuleringsperiode();
        heltUttak.setPoengArTom1991(5);
        heltUttak.setPoengArFom1992(9);
        heltUttak.setSluttpoengtall(2.5d);
        heltUttak.setAnvendtTrygdetid(31);
        heltUttak.setForholdstall(2.5);
        heltUttak.setUforegradVedOmregning(35);
        heltUttak.setDelytelser(List.of(
                new Delytelse("basisgp", 5000d),
                new Delytelse("basispt", 500d),
                new Delytelse("basistp", 5000d),
                new Delytelse("skjermingstillegg", 700d)
        ));
    }

    @Test
    void shouldMapWhenFulltForstegangsuttak() {
        forsteUttak.setDatoFom(createDate(2026, Calendar.JANUARY, 1));

        SimulertAP2011 simulertAP2011 = mapFulltUttak(forsteUttak, fnr);

        assertThat(simulertAP2011.getSimulertForsteuttak(), is(notNullValue()));
        assertThat(simulertAP2011.getSimulertHeltUttakEtter67Ar(), is(nullValue()));
    }

    @Test
    void shouldMapWhenGradertForstegangsuttak() {
        forsteUttak.setDatoFom(createDate(2027, Calendar.DECEMBER, 3));
        heltUttak.setDatoFom(createDate(2030, Calendar.DECEMBER, 3));

        SimulertAP2011 simulertAP2011 = mapGradertUttak(forsteUttak, heltUttak);

        assertThat(simulertAP2011.getSimulertForsteuttak().getPoengArTom1991(), is(15));
        assertThat(simulertAP2011.getSimulertForsteuttak().getPoengArFom1992(), is(17));
        assertThat(simulertAP2011.getSimulertForsteuttak().getSluttpoengtall(), is(5.5d));
        assertThat(simulertAP2011.getSimulertForsteuttak().getAnvendtTrygdetid(), is(64));
        assertThat(simulertAP2011.getSimulertForsteuttak().getBasisgp(), is(25000d));
        assertThat(simulertAP2011.getSimulertForsteuttak().getBasispt(), is(5000d));
        assertThat(simulertAP2011.getSimulertForsteuttak().getBasistp(), is(15000d));
        assertThat(simulertAP2011.getSimulertForsteuttak().getForholdstallUttak(), is(100.d));
        assertThat(simulertAP2011.getSimulertForsteuttak().getSkjermingstillegg(), is(7000d));
        assertThat(simulertAP2011.getSimulertForsteuttak().getUforegradVedOmregning(), is(10));

        assertThat(simulertAP2011.getSimulertHeltUttakEtter67Ar().getPoengArTom1991(), is(5));
        assertThat(simulertAP2011.getSimulertHeltUttakEtter67Ar().getPoengArFom1992(), is(9));
        assertThat(simulertAP2011.getSimulertHeltUttakEtter67Ar().getSluttpoengtall(), is(2.5d));
        assertThat(simulertAP2011.getSimulertHeltUttakEtter67Ar().getAnvendtTrygdetid(), is(31));
        assertThat(simulertAP2011.getSimulertHeltUttakEtter67Ar().getBasisgp(), is(5000d));
        assertThat(simulertAP2011.getSimulertHeltUttakEtter67Ar().getBasispt(), is(500d));
        assertThat(simulertAP2011.getSimulertHeltUttakEtter67Ar().getBasistp(), is(5000d));
        assertThat(simulertAP2011.getSimulertHeltUttakEtter67Ar().getForholdstallUttak(), is(2.5d));
        assertThat(simulertAP2011.getSimulertHeltUttakEtter67Ar().getSkjermingstillegg(), is(700d));
        assertThat(simulertAP2011.getSimulertHeltUttakEtter67Ar().getUforegradVedOmregning(), is(35));
    }

    @Test
    void gradertAndHeltUttakBefore67() {
        forsteUttak.setDatoFom(createDate(2022, Calendar.JANUARY, 1));
        heltUttak.setDatoFom(createDate(2024, Calendar.JUNE, 6));

        SimulertAP2011 simulertAP2011 = mapFulltUttak(forsteUttak, fnr);

        assertThat(simulertAP2011.getSimulertForsteuttak(), is(notNullValue()));
        assertThat(simulertAP2011.getSimulertForsteuttak().getPoengArFom1992(), is(17));
        assertThat(simulertAP2011.getSimulertHeltUttakEtter67Ar(), is(nullValue()));
    }

    @Test
    void gradertBefore67AndHeltUttakAfter67() {
        forsteUttak.setDatoFom(createDate(2024, Calendar.APRIL, 5));
        heltUttak.setDatoFom(createDate(2028, Calendar.OCTOBER, 11));

        SimulertAP2011 simulertAP2011 = mapGradertUttak(forsteUttak, heltUttak);

        assertThat(simulertAP2011.getSimulertForsteuttak(), is(notNullValue()));
        assertThat(simulertAP2011.getSimulertForsteuttak().getPoengArFom1992(), is(17));
        assertThat(simulertAP2011.getSimulertHeltUttakEtter67Ar(), is(notNullValue()));
        assertThat(simulertAP2011.getSimulertHeltUttakEtter67Ar().getPoengArFom1992(), is(9));
    }

    @Test
    void gradertAfter67AndHeltUttakAfter67() {
        forsteUttak.setDatoFom(createDate(2029, Calendar.FEBRUARY, 1));
        heltUttak.setDatoFom(createDate(2030, Calendar.MARCH, 8));

        SimulertAP2011 simulertAP2011 = mapGradertUttak(forsteUttak, heltUttak);

        assertThat(simulertAP2011.getSimulertForsteuttak(), is(notNullValue()));
        assertThat(simulertAP2011.getSimulertForsteuttak().getPoengArFom1992(), is(17));
        assertThat(simulertAP2011.getSimulertHeltUttakEtter67Ar(), is(notNullValue()));
        assertThat(simulertAP2011.getSimulertHeltUttakEtter67Ar().getPoengArFom1992(), is(9));
    }

    @Test
    void heltUttakBefore67() {
        forsteUttak.setDatoFom(createDate(2025, Calendar.FEBRUARY, 1));

        SimulertAP2011 simulertAP2011 = mapFulltUttak(forsteUttak, fnr);

        assertThat(simulertAP2011.getSimulertForsteuttak(), is(notNullValue()));
        assertThat(simulertAP2011.getSimulertForsteuttak().getPoengArFom1992(), is(17));
        assertThat(simulertAP2011.getSimulertHeltUttakEtter67Ar(), is(nullValue()));
    }

    @Test
    void heltUttakAfter67() {
        forsteUttak.setDatoFom(createDate(2029, Calendar.FEBRUARY, 1));

        SimulertAP2011 simulertAP2011 = mapFulltUttak(forsteUttak, fnr);

        assertThat(simulertAP2011.getSimulertForsteuttak(), is(notNullValue()));
        assertThat(simulertAP2011.getSimulertForsteuttak().getPoengArFom1992(), is(17));
        assertThat(simulertAP2011.getSimulertHeltUttakEtter67Ar(), is(notNullValue()));
        assertThat(simulertAP2011.getSimulertForsteuttak().getPoengArFom1992(), is(17));
    }
}