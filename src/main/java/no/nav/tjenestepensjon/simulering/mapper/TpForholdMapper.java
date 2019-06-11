package no.nav.tjenestepensjon.simulering.mapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.TpForhold;
import no.nav.tjenestepensjon.simulering.domain.Stillingsprosent;
import no.nav.tjenestepensjon.simulering.domain.TPOrdning;

public class TpForholdMapper {

    public static List<TpForhold> mapToTpForhold(Map<TPOrdning, List<Stillingsprosent>> tpOrdningStillingsprosentMap) {
        return tpOrdningStillingsprosentMap.keySet().stream()
                .map(tpOrdning -> mapToTpForhold(tpOrdning, tpOrdningStillingsprosentMap.get(tpOrdning))).collect(Collectors.toList());
    }

    private static TpForhold mapToTpForhold(TPOrdning tpOrdning, List<Stillingsprosent> stillingsprosentList) {
        TpForhold tpForhold = new TpForhold();
        tpForhold.setTpnr(tpOrdning.getTpId());
        tpForhold.setTssEksternId(tpOrdning.getTssId());
        tpForhold.getStillingsprosentListe().addAll(stillingsprosentList.stream()
                .map(StillingsprosentMapper::mapToStillingsprosent).collect(Collectors.toList()));
        return tpForhold;
    }
}
