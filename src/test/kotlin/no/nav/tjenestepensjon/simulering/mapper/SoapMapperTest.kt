package no.nav.tjenestepensjon.simulering.mapper

import AsyncExecutor.AsyncResponse
import OutgoingResponse.SimulertPensjon
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.ObjectFactory
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.SimulerOffentligTjenestepensjonRequest
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.SimulerTjenestepensjon
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.Stillingsprosent
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.v1.SimulerOffentligTjenestepensjon
import no.nav.tjenestepensjon.simulering.domain.Dateable
import no.nav.tjenestepensjon.simulering.model.v1.domain.TPOrdning
import no.nav.tjenestepensjon.simulering.mapper.SoapMapper.findAntallArInntektEtterHeltUttak
import no.nav.tjenestepensjon.simulering.mapper.SoapMapper.findInntektForUttak
import no.nav.tjenestepensjon.simulering.mapper.SoapMapper.findInntektOnDate
import no.nav.tjenestepensjon.simulering.mapper.SoapMapper.mapSimulerTjenestepensjonRequest
import no.nav.tjenestepensjon.simulering.model.v1.request.SimulerPensjonRequest
import no.nav.tjenestepensjon.simulering.model.v1.request.SimulerPensjonRequest.Delytelse
import no.nav.tjenestepensjon.simulering.model.v1.request.SimulerPensjonRequest.Inntekt
import no.nav.tjenestepensjon.simulering.model.v1.request.SimulerPensjonRequest.SimulerAfpPrivat
import no.nav.tjenestepensjon.simulering.model.v1.request.SimulerPensjonRequest.Simuleringsperiode
import no.nav.tjenestepensjon.simulering.util.Utils.createDate
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.*
import java.util.stream.Collectors
import javax.xml.datatype.DatatypeConfigurationException
import javax.xml.datatype.DatatypeFactory
import javax.xml.datatype.XMLGregorianCalendar

internal class SoapMapperTest {
    @Test
    fun sholdMapCommonValues() {
        simulerPensjonRequest.setSimuleringsperioder(java.util.List.of(createSimuleringsperiode(createDate(2020, Calendar.JANUARY, 1), 100)))
        val wrapperWrapper: SimulerOffentligTjenestepensjon = mapSimulerTjenestepensjonRequest(simulerPensjonRequest, tpOrdning, Map.of(tpOrdning, java.util.List.of(stillingsprosent)))
        val wrapper: SimulerOffentligTjenestepensjonRequest = wrapperWrapper.getRequest()
        val request: SimulerTjenestepensjon = wrapper.getSimulerTjenestepensjon()
        assertThat(request.getFnr(), `is`(simulerPensjonRequest.getFnr()))
        assertThat(request.getTssEksternId(), `is`(tpOrdning.getTssId()))
        assertThat(request.getTpnr(), `is`(tpOrdning.getTpId()))
        assertThat(request.getSivilstandKode(), `is`(simulerPensjonRequest.getSivilstandkode()))
        assertThat(request.getSprak(), `is`(simulerPensjonRequest.getSprak()))
        assertThat(request.getSimulertAFPOffentlig(), `is`(simulerPensjonRequest.getSimulertAFPOffentlig()))
        assertThat(request.getSimulertAFPPrivat().getAfpOpptjeningTotalbelop(), `is`(simulerPensjonRequest.getSimulertAFPPrivat().getAfpOpptjeningTotalbelop()))
        assertThat(request.getSimulertAFPPrivat().getKompensasjonstillegg(), `is`(simulerPensjonRequest.getSimulertAFPPrivat().getKompensasjonstillegg()))
    }

    @Test
    fun shouldMapFulltForsteUttak() {
        val fulltForsteUttak: Simuleringsperiode = createSimuleringsperiode(createDate(2020, Calendar.JANUARY, 1), 100)
        simulerPensjonRequest.setSimuleringsperioder(java.util.List.of(fulltForsteUttak))
        simulerPensjonRequest.setInntekter(java.util.List.of(Inntekt(fulltForsteUttak.getDatoFom(), 55000.0)))
        val wrapperWrapper: SimulerOffentligTjenestepensjon = mapSimulerTjenestepensjonRequest(simulerPensjonRequest, tpOrdning, Map.of(tpOrdning, java.util.List.of(stillingsprosent)))
        val wrapper: SimulerOffentligTjenestepensjonRequest = wrapperWrapper.getRequest()
        val request: SimulerTjenestepensjon = wrapper.getSimulerTjenestepensjon()
        assertThat(request.getForsteUttakDato(), Matchers.`is`(Matchers.equalTo(toXmlGregorianCalendar(fulltForsteUttak.getDatoFom()))))
        assertThat(request.getStillingsprosentOffGradertUttak(), Matchers.`is`(Matchers.nullValue()))
        assertThat(request.getStillingsprosentOffHeltUttak(), `is`(fulltForsteUttak.getStillingsprosentOffentlig()))
        assertThat(request.getUttaksgrad(), `is`(fulltForsteUttak.getUtg()))
        assertThat(request.getInntektUnderGradertUttak(), Matchers.`is`(Matchers.nullValue()))
        assertThat(request.getInntektEtterHeltUttak(), Matchers.`is`(55000))
        assertThat(request.getAntallArInntektEtterHeltUttak(), Matchers.`is`(0))
    }

    @Test
    fun shouldMapGradertForsteuttak() {
        val forsteUttak: Simuleringsperiode = createSimuleringsperiode(createDate(2020, Calendar.JANUARY, 1), 55)
        val heltUttak: Simuleringsperiode = createSimuleringsperiode(createDate(2024, Calendar.JANUARY, 1), 100)
        simulerPensjonRequest.setSimuleringsperioder(java.util.List.of(heltUttak, forsteUttak))
        simulerPensjonRequest.setInntekter(java.util.List.of(Inntekt(forsteUttak.getDatoFom(), 55000.0), Inntekt(heltUttak.getDatoFom(), 21000.0)))
        val wrapperWrapper: SimulerOffentligTjenestepensjon = mapSimulerTjenestepensjonRequest(simulerPensjonRequest, tpOrdning, Map.of(tpOrdning, java.util.List.of(stillingsprosent)))
        val wrapper: SimulerOffentligTjenestepensjonRequest = wrapperWrapper.getRequest()
        val request: SimulerTjenestepensjon = wrapper.getSimulerTjenestepensjon()
        assertThat(request.getForsteUttakDato(), Matchers.`is`(Matchers.equalTo(toXmlGregorianCalendar(forsteUttak.getDatoFom()))))
        assertThat(request.getStillingsprosentOffGradertUttak(), `is`(forsteUttak.getStillingsprosentOffentlig()))
        assertThat(request.getStillingsprosentOffHeltUttak(), `is`(heltUttak.getStillingsprosentOffentlig()))
        assertThat(request.getUttaksgrad(), `is`(forsteUttak.getUtg()))
        assertThat(request.getInntektUnderGradertUttak(), Matchers.`is`(55000))
        assertThat(request.getInntektEtterHeltUttak(), Matchers.`is`(21000))
        assertThat(request.getAntallArInntektEtterHeltUttak(), Matchers.`is`(0))
    }

    @Test
    fun shouldSortPeriodizedAscending() {
        val inntektList: List<Inntekt> = java.util.List.of<Inntekt>(
                Inntekt(createDate(2020, Calendar.JULY, 1), 2020.0),
                Inntekt(createDate(2018, Calendar.MARCH, 1), 2018.0),
                Inntekt(createDate(2017, Calendar.APRIL, 1), 2017.0),
                Inntekt(createDate(2029, Calendar.DECEMBER, 1), 2029.0),
                Inntekt(createDate(2027, Calendar.JULY, 1), 2027.0)
        )
        val sorted: List<Inntekt> = inntektList.stream().sorted(Dateable::sortAscendingByFomDato).collect(Collectors.toList<Any>())
        assertThat(sorted[0].getInntekt(), Matchers.`is`(2017.0))
        assertThat(sorted[4].getInntekt(), Matchers.`is`(2029.0))
    }

    @Test
    fun shouldFindInntektOnDate() {
        val inntektList: List<Inntekt> = java.util.List.of<Inntekt>(
                Inntekt(createDate(2020, Calendar.JULY, 1), 2020.0),
                Inntekt(createDate(2018, Calendar.MARCH, 1), 2018.0),
                Inntekt(createDate(2017, Calendar.APRIL, 1), 2017.0),
                Inntekt(createDate(2029, Calendar.DECEMBER, 1), 2029.0),
                Inntekt(createDate(2027, Calendar.JULY, 1), 2027.0)
        )
        assertThat(findInntektOnDate(inntektList, createDate(2020, Calendar.JULY, 1)).get().getInntekt(), Matchers.`is`(2020.0))
        assertThat(findInntektOnDate(inntektList, createDate(2029, Calendar.DECEMBER, 1)).get().getInntekt(), Matchers.`is`(2029.0))
        assertThat(findInntektOnDate(inntektList, createDate(2065, Calendar.APRIL, 1)).isPresent(), Matchers.`is`(false))
    }

    @Test
    fun shouldFindAntallArInntektEtterHeltUttak() {
        val inntektList: List<Inntekt> = java.util.List.of<Inntekt>(
                Inntekt(createDate(2020, Calendar.JULY, 1), 2020.0),
                Inntekt(createDate(2018, Calendar.MARCH, 1), 2018.0),
                Inntekt(createDate(2017, Calendar.APRIL, 1), 2017.0),
                Inntekt(createDate(2029, Calendar.DECEMBER, 1), 2029.0),
                Inntekt(createDate(2027, Calendar.JULY, 1), 2027.0)
        )
        assertThat(findAntallArInntektEtterHeltUttak(inntektList, createDate(2027, Calendar.JULY, 1)), Matchers.`is`(2029 - 2027))
        assertThat(findAntallArInntektEtterHeltUttak(inntektList, createDate(2018, Calendar.MARCH, 1)), Matchers.`is`(2029 - 2018))
        assertThat(findAntallArInntektEtterHeltUttak(inntektList, createDate(2029, Calendar.DECEMBER, 1)), Matchers.`is`(0))
        assertThat(findAntallArInntektEtterHeltUttak(inntektList, createDate(2014, Calendar.DECEMBER, 1)), Matchers.`is`(0))
    }

    @Test
    fun shoulFindInntektForUttak() {
        val inntektList: List<Inntekt> = java.util.List.of<Inntekt>(
                Inntekt(createDate(2020, Calendar.JULY, 1), 2020.0),
                Inntekt(createDate(2018, Calendar.MARCH, 1), 2018.0),
                Inntekt(createDate(2017, Calendar.APRIL, 1), 2017.0),
                Inntekt(createDate(2029, Calendar.DECEMBER, 1), 2029.0),
                Inntekt(createDate(2027, Calendar.JULY, 1), 2027.0)
        )
        assertThat(findInntektForUttak(inntektList, createDate(2018, Calendar.MARCH, 1)), Matchers.`is`(2017))
        assertThat(findInntektForUttak(inntektList, createDate(2020, Calendar.JULY, 1)), Matchers.`is`(2018))
        assertThat(findInntektForUttak(inntektList, createDate(2016, Calendar.JULY, 1)), Matchers.`is`(Matchers.nullValue()))
    }

    @Test
    fun mapSimulerTjenestepensjonResponse() {
        val utbetalingsperiode: `var` = ObjectFactory().createUtbetalingsperiode()
        utbetalingsperiode.setStartAlder(68)
        utbetalingsperiode.setSluttAlder(78)
        utbetalingsperiode.setStartManed(3)
        utbetalingsperiode.setSluttManed(2)
        utbetalingsperiode.setGrad(100)
        utbetalingsperiode.setArligUtbetaling(84000.0)
        utbetalingsperiode.setYtelseKode("AFP")
        utbetalingsperiode.setMangelfullSimuleringKode("ABC")
        val simulertPensjon: `var` = ObjectFactory().createSimulertPensjon()
        simulertPensjon.setTpnr("1234")
        simulertPensjon.setNavnOrdning("TP")
        simulertPensjon.setLeverandorUrl("TP_URL")
        simulertPensjon.getInkludertOrdningListe().add("TP_INK")
        simulertPensjon.getUtbetalingsperiodeListe().add(utbetalingsperiode)
        val simulertResponse: `var` = ObjectFactory().createSimulerOffentligTjenestepensjonResponse()
        simulertResponse.getSimulertPensjonListe().add(simulertPensjon)
        val response: `var` = ObjectFactory()
                .createSimulerOffentligTjenestepensjonResponse()
        response.setResponse(simulertResponse)
        val result: `var` = SoapMapper.mapSimulerTjenestepensjonResponse("14034800000", response).get(0)
        assertEquals("1234", result.getTpnr())
        assertEquals("TP", result.getNavnOrdning())
        assertEquals("TP_URL", result.getLeverandorUrl())
        Assertions.assertNull(result.getStatus())
        Assertions.assertNull(result.getFeilkode())
        Assertions.assertNull(result.getFeilbeskrivelse())
        assertEquals("TP_INK", result.getInkluderteOrdninger().get(0))
        Assertions.assertNull(result.getInkluderteTpnr())
        assertEquals(LocalDate.of(2016, 6, 1), result.getUtbetalingsperioder().get(0).getDatoFom())
        assertEquals(LocalDate.of(2026, 5, 31), result.getUtbetalingsperioder().get(0).getDatoTom())
        assertEquals(100, result.getUtbetalingsperioder().get(0).getGrad())
        assertEquals(84000.0, result.getUtbetalingsperioder().get(0).getArligUtbetaling())
        assertEquals("AFP", result.getUtbetalingsperioder().get(0).getYtelsekode())
        assertEquals("ABC", result.getUtbetalingsperioder().get(0).getMangelfullSimuleringkode())
    }

    companion object {
        private var simulerPensjonRequest: SimulerPensjonRequest? = null
        private val tpOrdning: TPOrdning = TPOrdning("tssId", "tpId")
        private val stillingsprosent: Stillingsprosent = Stillingsprosent()
        @BeforeAll
        fun beforeAll() {
            simulerPensjonRequest = SimulerPensjonRequest()
            simulerPensjonRequest.setFnr("01016092500")
            simulerPensjonRequest.setSivilstandkode("GIFT")
            simulerPensjonRequest.setSprak("NO")
            simulerPensjonRequest.setSimulertAFPOffentlig(5000)
            simulerPensjonRequest.setSimulertAFPPrivat(
                    SimulerAfpPrivat(245000, 5000.0))
            simulerPensjonRequest.setInntekter(java.util.List.of())
            stillingsprosent.setDatoFom(LocalDate.now())
            stillingsprosent.setDatoTom(LocalDate.now())
        }

        private fun createSimuleringsperiode(date: Date, uttaksgrad: Int): Simuleringsperiode {
            val simuleringsperiode = Simuleringsperiode()
            simuleringsperiode.setDatoFom(date)
            simuleringsperiode.setUtg(uttaksgrad)
            simuleringsperiode.setPoengArTom1991(15)
            simuleringsperiode.setPoengArFom1992(17)
            simuleringsperiode.setSluttpoengtall(5.5)
            simuleringsperiode.setAnvendtTrygdetid(64)
            simuleringsperiode.setForholdstall(100.0)
            simuleringsperiode.setUforegradVedOmregning(10)
            simuleringsperiode.setDelytelser(java.util.List.of(
                    Delytelse("basisgp", 25000.0),
                    Delytelse("basispt", 5000.0),
                    Delytelse("basistp", 15000.0),
                    Delytelse("skjermingstillegg", 7000.0)
            ))
            return simuleringsperiode
        }

        private fun toXmlGregorianCalendar(date: Date): XMLGregorianCalendar {
            val gregorianCalendar = GregorianCalendar()
            gregorianCalendar.time = date
            return try {
                DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar)
            } catch (e: DatatypeConfigurationException) {
                throw RuntimeException("Exception while converting $date to XMLGregorianCalendar", e)
            }
        }
    }
}