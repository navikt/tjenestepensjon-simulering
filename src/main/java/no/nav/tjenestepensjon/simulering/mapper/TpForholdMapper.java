package no.nav.tjenestepensjon.simulering.mapper;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.ekstern.pensjon.tjenester.tjenestepensjonsimulering.meldinger.v1.TpForhold;
import no.nav.tjenestepensjon.simulering.domain.TPOrdning;

public class TpForholdMapper {

    public static List<TpForhold> mapToTpForhold(List<TPOrdning> tpOrdningList) {
        return tpOrdningList.stream().map(TpForholdMapper::mapToTpForhold).collect(Collectors.toList());
    }

    private static TpForhold mapToTpForhold(TPOrdning tpOrdning) {
        TpForhold tpForhold = new TpForhold();
        tpForhold.setTpnr(tpOrdning.getTpId());
        tpForhold.setTssEksternId(tpOrdning.getTssId());
        tpForhold.getStillingsprosentListe().addAll(tpOrdning.getStillingsprosentList().stream()
                .map(StillingsprosentMapper::mapToStillingsprosent).collect(Collectors.toList()));
        return tpForhold;
    }
}
