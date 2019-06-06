package no.nav.tjenestepensjon.simulering.mapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import static no.nav.tjenestepensjon.simulering.util.Utils.createDate;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.SimulerOffentligTjenestepensjonRequest;
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.SimulerTjenestepensjon;
import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.v1.SimulerOffentligTjenestepensjon;
import no.nav.tjenestepensjon.simulering.domain.Dateable;
import no.nav.tjenestepensjon.simulering.domain.TPOrdning;
import no.nav.tjenestepensjon.simulering.rest.IncomingRequest;
import no.nav.tjenestepensjon.simulering.rest.IncomingRequest.Delytelse;
import no.nav.tjenestepensjon.simulering.rest.IncomingRequest.Inntekt;
import no.nav.tjenestepensjon.simulering.rest.IncomingRequest.SimulerAfpPrivat;
import no.nav.tjenestepensjon.simulering.rest.IncomingRequest.Simuleringsperiode;

class SoapMapperTest {

    private static IncomingRequest incomingRequest;
    private static final TPOrdning tpOrdning = new TPOrdning("tssId", "tpId");

    @BeforeAll
    static void beforeAll() {
        incomingRequest = new IncomingRequest();
        incomingRequest.setFnr("01016092500");
        incomingRequest.setSivilstandkode("GIFT");
        incomingRequest.setSprak("NO");
        incomingRequest.setSimulertAFPOffentlig(5000);
        incomingRequest.setSimulertAFPPrivat(
                new SimulerAfpPrivat(245000, 5000d));
        incomingRequest.setInntekter(List.of());
    }

    @Test
    void sholdMapCommonValues() {
        incomingRequest.setSimuleringsperioder(List.of(createSimuleringsperiode(createDate(2020, Calendar.JANUARY, 1), 100)));

        SimulerOffentligTjenestepensjon wrapperWrapper = SoapMapper.mapSimulerTjenestepensjonRequest(incomingRequest, tpOrdning);
        SimulerOffentligTjenestepensjonRequest wrapper = wrapperWrapper.getRequest();
        SimulerTjenestepensjon request = wrapper.getSimulerTjenestepensjon();

        assertThat(request.getFnr(), is(incomingRequest.getFnr()));
        assertThat(request.getTssEksternId(), is(tpOrdning.getTssId()));
        assertThat(request.getTpnr(), is(tpOrdning.getTpId()));
        assertThat(request.getSivilstandKode(), is(incomingRequest.getSivilstandkode()));
        assertThat(request.getSprak(), is(incomingRequest.getSprak()));
        assertThat(request.getSimulertAFPOffentlig(), is(incomingRequest.getSimulertAFPOffentlig()));
        assertThat(request.getSimulertAFPPrivat().getAfpOpptjeningTotalbelop(), is(incomingRequest.getSimulertAFPPrivat().getAfpOpptjeningTotalbelop()));
        assertThat(request.getSimulertAFPPrivat().getKompensasjonstillegg(), is(incomingRequest.getSimulertAFPPrivat().getKompensasjonstillegg()));
    }

    @Test
    void shouldMapFulltForsteUttak() {
        Simuleringsperiode fulltForsteUttak = createSimuleringsperiode(createDate(2020, Calendar.JANUARY, 1), 100);
        incomingRequest.setSimuleringsperioder(List.of(fulltForsteUttak));
        incomingRequest.setInntekter(List.of(new Inntekt(fulltForsteUttak.getDatoFom(), 55000d)));

        SimulerOffentligTjenestepensjon wrapperWrapper = SoapMapper.mapSimulerTjenestepensjonRequest(incomingRequest, tpOrdning);
        SimulerOffentligTjenestepensjonRequest wrapper = wrapperWrapper.getRequest();
        SimulerTjenestepensjon request = wrapper.getSimulerTjenestepensjon();

        assertThat(request.getForsteUttakDato(), is(equalTo(toXmlGregorianCalendar(fulltForsteUttak.getDatoFom()))));
        assertThat(request.getStillingsprosentOffGradertUttak(), is(nullValue()));
        assertThat(request.getStillingsprosentOffHeltUttak(), is(fulltForsteUttak.getStillingsprosentOffentlig()));
        assertThat(request.getUttaksgrad(), is(fulltForsteUttak.getUtg()));
        assertThat(request.getInntektUnderGradertUttak(), is(nullValue()));
        assertThat(request.getInntektEtterHeltUttak(), is(55000));
        assertThat(request.getAntallArInntektEtterHeltUttak(), is(0));
    }

    @Test
    void shouldMapGradertForsteuttak() {
        Simuleringsperiode forsteUttak = createSimuleringsperiode(createDate(2020, Calendar.JANUARY, 1), 55);
        Simuleringsperiode heltUttak = createSimuleringsperiode(createDate(2024, Calendar.JANUARY, 1), 100);
        incomingRequest.setSimuleringsperioder(List.of(heltUttak, forsteUttak));
        incomingRequest.setInntekter(List.of(new Inntekt(forsteUttak.getDatoFom(), 55000d), new Inntekt(heltUttak.getDatoFom(), 21000d)));

        SimulerOffentligTjenestepensjon wrapperWrapper = SoapMapper.mapSimulerTjenestepensjonRequest(incomingRequest, tpOrdning);
        SimulerOffentligTjenestepensjonRequest wrapper = wrapperWrapper.getRequest();
        SimulerTjenestepensjon request = wrapper.getSimulerTjenestepensjon();

        assertThat(request.getForsteUttakDato(), is(equalTo(toXmlGregorianCalendar(forsteUttak.getDatoFom()))));
        assertThat(request.getStillingsprosentOffGradertUttak(), is(forsteUttak.getStillingsprosentOffentlig()));
        assertThat(request.getStillingsprosentOffHeltUttak(), is(heltUttak.getStillingsprosentOffentlig()));
        assertThat(request.getUttaksgrad(), is(forsteUttak.getUtg()));
        assertThat(request.getInntektUnderGradertUttak(), is(55000));
        assertThat(request.getInntektEtterHeltUttak(), is(21000));
        assertThat(request.getAntallArInntektEtterHeltUttak(), is(0));
    }

    @Test
    void shouldSortPeriodizedAscending() {
        List<Inntekt> inntektList = List.of(
                new Inntekt(createDate(2020, Calendar.JULY, 1), 2020d),
                new Inntekt(createDate(2018, Calendar.MARCH, 1), 2018d),
                new Inntekt(createDate(2017, Calendar.APRIL, 1), 2017d),
                new Inntekt(createDate(2029, Calendar.DECEMBER, 1), 2029d),
                new Inntekt(createDate(2027, Calendar.JULY, 1), 2027d)
        );

        List<Inntekt> sorted = inntektList.stream().sorted(Dateable::sortAscendingByFomDato).collect(Collectors.toList());
        assertThat(sorted.get(0).getInntekt(), is(2017d));
        assertThat(sorted.get(4).getInntekt(), is(2029d));
    }

    @Test
    void shouldFindInntektOnDate() {
        List<Inntekt> inntektList = List.of(
                new Inntekt(createDate(2020, Calendar.JULY, 1), 2020d),
                new Inntekt(createDate(2018, Calendar.MARCH, 1), 2018d),
                new Inntekt(createDate(2017, Calendar.APRIL, 1), 2017d),
                new Inntekt(createDate(2029, Calendar.DECEMBER, 1), 2029d),
                new Inntekt(createDate(2027, Calendar.JULY, 1), 2027d)
        );

        assertThat(SoapMapper.findInntektOnDate(inntektList, createDate(2020, Calendar.JULY, 1)).get().getInntekt(), is(2020d));
        assertThat(SoapMapper.findInntektOnDate(inntektList, createDate(2029, Calendar.DECEMBER, 1)).get().getInntekt(), is(2029d));
        assertThat(SoapMapper.findInntektOnDate(inntektList, createDate(2065, Calendar.APRIL, 1)).isPresent(), is(false));
    }

    @Test
    void shouldFindAntallArInntektEtterHeltUttak() {
        List<Inntekt> inntektList = List.of(
                new Inntekt(createDate(2020, Calendar.JULY, 1), 2020d),
                new Inntekt(createDate(2018, Calendar.MARCH, 1), 2018d),
                new Inntekt(createDate(2017, Calendar.APRIL, 1), 2017d),
                new Inntekt(createDate(2029, Calendar.DECEMBER, 1), 2029d),
                new Inntekt(createDate(2027, Calendar.JULY, 1), 2027d)
        );

        assertThat(SoapMapper.findAntallArInntektEtterHeltUttak(inntektList, createDate(2027, Calendar.JULY, 1)), is(2029 - 2027));
        assertThat(SoapMapper.findAntallArInntektEtterHeltUttak(inntektList, createDate(2018, Calendar.MARCH, 1)), is(2029 - 2018));
        assertThat(SoapMapper.findAntallArInntektEtterHeltUttak(inntektList, createDate(2029, Calendar.DECEMBER, 1)), is(0));
        assertThat(SoapMapper.findAntallArInntektEtterHeltUttak(inntektList, createDate(2014, Calendar.DECEMBER, 1)), is(0));
    }

    @Test
    void shoulFindInntektForUttak() {
        List<Inntekt> inntektList = List.of(
                new Inntekt(createDate(2020, Calendar.JULY, 1), 2020d),
                new Inntekt(createDate(2018, Calendar.MARCH, 1), 2018d),
                new Inntekt(createDate(2017, Calendar.APRIL, 1), 2017d),
                new Inntekt(createDate(2029, Calendar.DECEMBER, 1), 2029d),
                new Inntekt(createDate(2027, Calendar.JULY, 1), 2027d)
        );

        assertThat(SoapMapper.findInntektForUttak(inntektList, createDate(2018, Calendar.MARCH, 1)), is(2017));
        assertThat(SoapMapper.findInntektForUttak(inntektList, createDate(2020, Calendar.JULY, 1)), is(2018));
        assertThat(SoapMapper.findInntektForUttak(inntektList, createDate(2016, Calendar.JULY, 1)), is(nullValue()));
    }

    private static Simuleringsperiode createSimuleringsperiode(Date date, Integer uttaksgrad) {
        Simuleringsperiode simuleringsperiode = new Simuleringsperiode();
        simuleringsperiode.setDatoFom(date);
        simuleringsperiode.setUtg(uttaksgrad);
        simuleringsperiode.setPoengArTom1991(15);
        simuleringsperiode.setPoengArFom1992(17);
        simuleringsperiode.setSluttpoengtall(5.5d);
        simuleringsperiode.setAnvendtTrygdetid(64);
        simuleringsperiode.setForholdstall(100.0);
        simuleringsperiode.setUforegradVedOmregning(10);
        simuleringsperiode.setDelytelser(List.of(
                new Delytelse("basisgp", 25000d),
                new Delytelse("basispt", 5000d),
                new Delytelse("basistp", 15000d),
                new Delytelse("skjermingstillegg", 7000d)
        ));
        return simuleringsperiode;
    }

    private static XMLGregorianCalendar toXmlGregorianCalendar(Date date) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(date);
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException("Exception while converting " + date + " to XMLGregorianCalendar", e);
        }
    }
}