package no.nav.tjenestepensjon.simulering.mapper;

import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.ObjectFactory;
import no.nav.tjenestepensjon.simulering.util.Utils;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class SoapMapperTests {

    @Test
    public void mapSimulerTjenestepensjonResponse() {
        var utbetalingsperiode = new ObjectFactory().createUtbetalingsperiode();
        utbetalingsperiode.setStartAlder(68);
        utbetalingsperiode.setSluttAlder(78);
        utbetalingsperiode.setStartManed(3);
        utbetalingsperiode.setSluttManed(2);
        utbetalingsperiode.setGrad(100);
        utbetalingsperiode.setArligUtbetaling(84000.0);
        utbetalingsperiode.setYtelseKode("AFP");
        utbetalingsperiode.setMangelfullSimuleringKode("ABC");

        var simulertPensjon = new ObjectFactory().createSimulertPensjon();
        simulertPensjon.setTpnr("1234");
        simulertPensjon.setNavnOrdning("TP");
        simulertPensjon.setLeverandorUrl("TP_URL");
        simulertPensjon.getInkludertOrdningListe().add("TP_INK");
        simulertPensjon.getUtbetalingsperiodeListe().add(utbetalingsperiode);

        var simulertResponse = new ObjectFactory().createSimulerOffentligTjenestepensjonResponse();
        simulertResponse.getSimulertPensjonListe().add(simulertPensjon);

        var response = new no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.v1.ObjectFactory()
            .createSimulerOffentligTjenestepensjonResponse();
        response.setResponse(simulertResponse);

        var result = SoapMapper.mapSimulerTjenestepensjonResponse("14034800000", response).get(0);

        assertEquals("1234", result.getTpnr());
        assertEquals("TP", result.getNavnOrdning());
        assertEquals("TP_URL", result.getLeverandorUrl());
        assertNull(result.getStatus());
        assertNull(result.getFeilkode());
        assertNull(result.getFeilbeskrivelse());
        assertEquals("TP_INK", result.getInkluderteOrdninger().get(0));
        assertNull(result.getInkluderteTpnr());
        assertEquals(Utils.createDate(2016, Calendar.JUNE, 1), result.getUtbetalingsperioder().get(0).getStartDato());
        assertEquals(Utils.createDate(2026, Calendar.MAY, 1), result.getUtbetalingsperioder().get(0).getSluttDato());
        assertEquals(100, result.getUtbetalingsperioder().get(0).getGrad());
        assertEquals(84000.0, result.getUtbetalingsperioder().get(0).getArligUtbetaling());
        assertEquals("AFP", result.getUtbetalingsperioder().get(0).getYtelsekode());
        assertEquals("ABC", result.getUtbetalingsperioder().get(0).getMangelfullSimuleringkode());
    }
}
