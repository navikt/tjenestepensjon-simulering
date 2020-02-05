package no.nav.tjenestepensjon.simulering.mapper

import AsyncExecutor.AsyncResponse
import OutgoingResponse.SimulertPensjon
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.SimulertAP2011
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.Stillingsprosent
import no.nav.tjenestepensjon.simulering.mapper.SimulertAP2011Mapper.mapFulltUttak
import no.nav.tjenestepensjon.simulering.mapper.SimulertAP2011Mapper.mapGradertUttak
import no.nav.tjenestepensjon.simulering.model.v1.request.SimulerPensjonRequest.Delytelse
import no.nav.tjenestepensjon.simulering.model.v1.request.SimulerPensjonRequest.Simuleringsperiode
import no.nav.tjenestepensjon.simulering.util.Utils.createDate
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

internal class SimulertAP2011MapperTest {
    @BeforeEach
    fun beforeAll() {
        forsteUttak = Simuleringsperiode()
        forsteUttak.setPoengArTom1991(15)
        forsteUttak.setPoengArFom1992(17)
        forsteUttak.setSluttpoengtall(5.5)
        forsteUttak.setAnvendtTrygdetid(64)
        forsteUttak.setForholdstall(100.0)
        forsteUttak.setUforegradVedOmregning(10)
        forsteUttak.setDelytelser(List.of(
                Delytelse("basisgp", 25000.0),
                Delytelse("basispt", 5000.0),
                Delytelse("basistp", 15000.0),
                Delytelse("skjermingstillegg", 7000.0)
        ))
        heltUttak = Simuleringsperiode()
        heltUttak.setPoengArTom1991(5)
        heltUttak.setPoengArFom1992(9)
        heltUttak.setSluttpoengtall(2.5)
        heltUttak.setAnvendtTrygdetid(31)
        heltUttak.setForholdstall(2.5)
        heltUttak.setUforegradVedOmregning(35)
        heltUttak.setDelytelser(List.of(
                Delytelse("basisgp", 5000.0),
                Delytelse("basispt", 500.0),
                Delytelse("basistp", 5000.0),
                Delytelse("skjermingstillegg", 700.0)
        ))
    }

    @Test
    fun shouldMapWhenFulltForstegangsuttak() {
        forsteUttak.setDatoFom(createDate(2026, Calendar.JANUARY, 1))
        val simulertAP2011: SimulertAP2011 = mapFulltUttak(forsteUttak, fnr)
        assertThat(simulertAP2011.getSimulertForsteuttak(), Matchers.`is`(Matchers.notNullValue()))
        assertThat(simulertAP2011.getSimulertHeltUttakEtter67Ar(), Matchers.`is`(Matchers.nullValue()))
    }

    @Test
    fun shouldMapWhenGradertForstegangsuttak() {
        forsteUttak.setDatoFom(createDate(2027, Calendar.DECEMBER, 3))
        heltUttak.setDatoFom(createDate(2030, Calendar.DECEMBER, 3))
        val simulertAP2011: SimulertAP2011 = mapGradertUttak(forsteUttak, heltUttak)
        assertThat(simulertAP2011.getSimulertForsteuttak().getPoengArTom1991(), Matchers.`is`(15))
        assertThat(simulertAP2011.getSimulertForsteuttak().getPoengArFom1992(), Matchers.`is`(17))
        assertThat(simulertAP2011.getSimulertForsteuttak().getSluttpoengtall(), Matchers.`is`(5.5))
        assertThat(simulertAP2011.getSimulertForsteuttak().getAnvendtTrygdetid(), Matchers.`is`(64))
        assertThat(simulertAP2011.getSimulertForsteuttak().getBasisgp(), Matchers.`is`(25000.0))
        assertThat(simulertAP2011.getSimulertForsteuttak().getBasispt(), Matchers.`is`(5000.0))
        assertThat(simulertAP2011.getSimulertForsteuttak().getBasistp(), Matchers.`is`(15000.0))
        assertThat(simulertAP2011.getSimulertForsteuttak().getForholdstallUttak(), Matchers.`is`(100.0))
        assertThat(simulertAP2011.getSimulertForsteuttak().getSkjermingstillegg(), Matchers.`is`(7000.0))
        assertThat(simulertAP2011.getSimulertForsteuttak().getUforegradVedOmregning(), Matchers.`is`(10))
        assertThat(simulertAP2011.getSimulertHeltUttakEtter67Ar().getPoengArTom1991(), Matchers.`is`(5))
        assertThat(simulertAP2011.getSimulertHeltUttakEtter67Ar().getPoengArFom1992(), Matchers.`is`(9))
        assertThat(simulertAP2011.getSimulertHeltUttakEtter67Ar().getSluttpoengtall(), Matchers.`is`(2.5))
        assertThat(simulertAP2011.getSimulertHeltUttakEtter67Ar().getAnvendtTrygdetid(), Matchers.`is`(31))
        assertThat(simulertAP2011.getSimulertHeltUttakEtter67Ar().getBasisgp(), Matchers.`is`(5000.0))
        assertThat(simulertAP2011.getSimulertHeltUttakEtter67Ar().getBasispt(), Matchers.`is`(500.0))
        assertThat(simulertAP2011.getSimulertHeltUttakEtter67Ar().getBasistp(), Matchers.`is`(5000.0))
        assertThat(simulertAP2011.getSimulertHeltUttakEtter67Ar().getForholdstallUttak(), Matchers.`is`(2.5))
        assertThat(simulertAP2011.getSimulertHeltUttakEtter67Ar().getSkjermingstillegg(), Matchers.`is`(700.0))
        assertThat(simulertAP2011.getSimulertHeltUttakEtter67Ar().getUforegradVedOmregning(), Matchers.`is`(35))
    }

    @Test
    fun gradertAndHeltUttakBefore67() {
        forsteUttak.setDatoFom(createDate(2022, Calendar.JANUARY, 1))
        heltUttak.setDatoFom(createDate(2024, Calendar.JUNE, 6))
        val simulertAP2011: SimulertAP2011 = mapFulltUttak(forsteUttak, fnr)
        assertThat(simulertAP2011.getSimulertForsteuttak(), Matchers.`is`(Matchers.notNullValue()))
        assertThat(simulertAP2011.getSimulertForsteuttak().getPoengArFom1992(), Matchers.`is`(17))
        assertThat(simulertAP2011.getSimulertHeltUttakEtter67Ar(), Matchers.`is`(Matchers.nullValue()))
    }

    @Test
    fun gradertBefore67AndHeltUttakAfter67() {
        forsteUttak.setDatoFom(createDate(2024, Calendar.APRIL, 5))
        heltUttak.setDatoFom(createDate(2028, Calendar.OCTOBER, 11))
        val simulertAP2011: SimulertAP2011 = mapGradertUttak(forsteUttak, heltUttak)
        assertThat(simulertAP2011.getSimulertForsteuttak(), Matchers.`is`(Matchers.notNullValue()))
        assertThat(simulertAP2011.getSimulertForsteuttak().getPoengArFom1992(), Matchers.`is`(17))
        assertThat(simulertAP2011.getSimulertHeltUttakEtter67Ar(), Matchers.`is`(Matchers.notNullValue()))
        assertThat(simulertAP2011.getSimulertHeltUttakEtter67Ar().getPoengArFom1992(), Matchers.`is`(9))
    }

    @Test
    fun gradertAfter67AndHeltUttakAfter67() {
        forsteUttak.setDatoFom(createDate(2029, Calendar.FEBRUARY, 1))
        heltUttak.setDatoFom(createDate(2030, Calendar.MARCH, 8))
        val simulertAP2011: SimulertAP2011 = mapGradertUttak(forsteUttak, heltUttak)
        assertThat(simulertAP2011.getSimulertForsteuttak(), Matchers.`is`(Matchers.notNullValue()))
        assertThat(simulertAP2011.getSimulertForsteuttak().getPoengArFom1992(), Matchers.`is`(17))
        assertThat(simulertAP2011.getSimulertHeltUttakEtter67Ar(), Matchers.`is`(Matchers.notNullValue()))
        assertThat(simulertAP2011.getSimulertHeltUttakEtter67Ar().getPoengArFom1992(), Matchers.`is`(9))
    }

    @Test
    fun heltUttakBefore67() {
        forsteUttak.setDatoFom(createDate(2025, Calendar.FEBRUARY, 1))
        val simulertAP2011: SimulertAP2011 = mapFulltUttak(forsteUttak, fnr)
        assertThat(simulertAP2011.getSimulertForsteuttak(), Matchers.`is`(Matchers.notNullValue()))
        assertThat(simulertAP2011.getSimulertForsteuttak().getPoengArFom1992(), Matchers.`is`(17))
        assertThat(simulertAP2011.getSimulertHeltUttakEtter67Ar(), Matchers.`is`(Matchers.nullValue()))
    }

    @Test
    fun heltUttakAfter67() {
        forsteUttak.setDatoFom(createDate(2029, Calendar.FEBRUARY, 1))
        val simulertAP2011: SimulertAP2011 = mapFulltUttak(forsteUttak, fnr)
        assertThat(simulertAP2011.getSimulertForsteuttak(), Matchers.`is`(Matchers.notNullValue()))
        assertThat(simulertAP2011.getSimulertForsteuttak().getPoengArFom1992(), Matchers.`is`(17))
        assertThat(simulertAP2011.getSimulertHeltUttakEtter67Ar(), Matchers.`is`(Matchers.notNullValue()))
        assertThat(simulertAP2011.getSimulertForsteuttak().getPoengArFom1992(), Matchers.`is`(17))
    }

    companion object {
        private var forsteUttak: Simuleringsperiode? = null
        private var heltUttak: Simuleringsperiode? = null
        //Turns 67 in 2027
        private const val fnr = "01016092500"
    }
}