package no.nav.tjenestepensjon.simulering.domain;

public class TPOrdning {
    private final String tssId;
    private final String tpId;

    public TPOrdning(String tssId, String tpId) {
        this.tssId = tssId;
        this.tpId = tpId;
    }

    public String getTssId() {
        return tssId;
    }

    public String getTpId() {
        return tpId;
    }
}
