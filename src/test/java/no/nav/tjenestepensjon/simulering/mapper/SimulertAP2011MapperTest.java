package no.nav.tjenestepensjon.simulering.mapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import static no.nav.tjenestepensjon.simulering.mapper.SimulertAP2011Mapper.mapToSimulertAP2011;
import static no.nav.tjenestepensjon.simulering.rest.IncomingRequest.Simuleringsperiode;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.SimulertAP2011;
import no.nav.tjenestepensjon.simulering.rest.IncomingRequest.Delytelse;

class SimulertAP2011MapperTest {

    private static Simuleringsperiode periode1;
    private static Simuleringsperiode periode2;

    @BeforeAll
    static void beforeAll() {
        periode1 = new Simuleringsperiode();
        periode1.setDatoFom(Date.from(Instant.parse("2007-12-03T10:15:30.00Z")));
        periode1.setPoengArTom1991(15);
        periode1.setPoengArFom1992(17);
        periode1.setSluttpoengtall(5.5d);
        periode1.setAnvendtTrygdetid(64);
        periode1.setForholdstall(100.0);
        periode1.setUforegradVedOmregning(10);
        periode1.setDelytelser(List.of(
                new Delytelse("basisgp", 25000d),
                new Delytelse("basispt", 5000d),
                new Delytelse("basistp", 15000d),
                new Delytelse("skjermingstillegg", 7000d)
        ));

        periode2 = new Simuleringsperiode();
        periode2.setDatoFom(Date.from(Instant.parse("2015-12-03T10:15:30.00Z")));
        periode2.setPoengArTom1991(5);
        periode2.setPoengArFom1992(7);
        periode2.setSluttpoengtall(2.5d);
        periode2.setAnvendtTrygdetid(31);
        periode2.setForholdstall(2.5);
        periode2.setUforegradVedOmregning(35);
        periode2.setDelytelser(List.of(
                new Delytelse("basisgp", 5000d),
                new Delytelse("basispt", 500d),
                new Delytelse("basistp", 5000d),
                new Delytelse("skjermingstillegg", 700d)
        ));
    }

    @Test
    void sortsFomDateAscending() {
        Simuleringsperiode earliest = new Simuleringsperiode();
        earliest.setDatoFom(Date.from(Instant.parse("2005-12-03T10:15:30.00Z")));
        earliest.setPoengArTom1991(1);
        earliest.setPoengArFom1992(1);
        earliest.setSluttpoengtall(1.0);
        earliest.setAnvendtTrygdetid(1);
        earliest.setForholdstall(1.0);
        earliest.setDelytelser(List.of());

        Simuleringsperiode latest = new Simuleringsperiode();
        latest.setDatoFom(Date.from(Instant.parse("2067-12-03T10:15:30.00Z")));
        latest.setPoengArTom1991(2);
        latest.setPoengArFom1992(2);
        latest.setSluttpoengtall(2.0);
        latest.setAnvendtTrygdetid(2);
        latest.setForholdstall(2.0);
        latest.setDelytelser(List.of());

        SimulertAP2011 simulertAP2011 = mapToSimulertAP2011(List.of(periode2, earliest, latest, periode1));

        assertThat(simulertAP2011.getSimulertForsteuttak().getPoengArFom1992(), is(1));
        assertThat(simulertAP2011.getSimulertForsteuttak().getPoengArFom1992(), not(periode1.getPoengArFom1992()));
        assertThat(simulertAP2011.getSimulertForsteuttak().getPoengArFom1992(), not(periode2.getPoengArFom1992()));
        assertThat(simulertAP2011.getSimulertHeltUttakEtter67Ar().getPoengArFom1992(), is(2));
        assertThat(simulertAP2011.getSimulertHeltUttakEtter67Ar().getPoengArFom1992(), not(periode1.getPoengArFom1992()));
        assertThat(simulertAP2011.getSimulertHeltUttakEtter67Ar().getPoengArFom1992(), not(periode2.getPoengArFom1992()));
    }

    @Test
    void shouldMapWhenFulltForstegangsuttak() {
        SimulertAP2011 simulertAP2011 = mapToSimulertAP2011(List.of(periode1));

        assertThat(simulertAP2011.getSimulertForsteuttak(), is(notNullValue()));
        assertThat(simulertAP2011.getSimulertHeltUttakEtter67Ar(), is(nullValue()));
    }

    @Test
    void shouldMapWhenGradertForstegangsuttak() {
        SimulertAP2011 simulertAP2011 = mapToSimulertAP2011(List.of(periode1, periode2));

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
        assertThat(simulertAP2011.getSimulertHeltUttakEtter67Ar().getPoengArFom1992(), is(7));
        assertThat(simulertAP2011.getSimulertHeltUttakEtter67Ar().getSluttpoengtall(), is(2.5d));
        assertThat(simulertAP2011.getSimulertHeltUttakEtter67Ar().getAnvendtTrygdetid(), is(31));
        assertThat(simulertAP2011.getSimulertHeltUttakEtter67Ar().getBasisgp(), is(5000d));
        assertThat(simulertAP2011.getSimulertHeltUttakEtter67Ar().getBasispt(), is(500d));
        assertThat(simulertAP2011.getSimulertHeltUttakEtter67Ar().getBasistp(), is(5000d));
        assertThat(simulertAP2011.getSimulertHeltUttakEtter67Ar().getForholdstallUttak(), is(2.5d));
        assertThat(simulertAP2011.getSimulertHeltUttakEtter67Ar().getSkjermingstillegg(), is(700d));
        assertThat(simulertAP2011.getSimulertHeltUttakEtter67Ar().getUforegradVedOmregning(), is(35));
    }
}