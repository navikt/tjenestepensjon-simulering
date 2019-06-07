package no.nav.tjenestepensjon.simulering.mapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import static no.nav.tjenestepensjon.simulering.domain.TpLeverandor.EndpointImpl.SOAP;
import static no.nav.tjenestepensjon.simulering.mapper.TpForholdMapper.mapToTpForhold;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.TpForhold;
import no.nav.tjenestepensjon.simulering.domain.Stillingsprosent;
import no.nav.tjenestepensjon.simulering.domain.TPOrdning;
import no.nav.tjenestepensjon.simulering.domain.TpLeverandor;

class TpForholdMapperTest {

    @Test
    void mapsTpOrdningToTpForhold() {
        Stillingsprosent stillingsprosent = new Stillingsprosent();
        stillingsprosent.setDatoFom(LocalDate.now());
        stillingsprosent.setDatoTom(LocalDate.now());
        TPOrdning tpOrdning = new TPOrdning("tssId", "tpId", new TpLeverandor("", "", SOAP), List.of(stillingsprosent));

        List<TpForhold> tpForhold = mapToTpForhold(List.of(tpOrdning));
        TpForhold mapped = tpForhold.get(0);
        assertThat(mapped.getTpnr(), is(tpOrdning.getTpId()));
        assertThat(mapped.getTssEksternId(), is(tpOrdning.getTssId()));
        assertThat(mapped.getStillingsprosentListe().size(), is(1));
    }
}