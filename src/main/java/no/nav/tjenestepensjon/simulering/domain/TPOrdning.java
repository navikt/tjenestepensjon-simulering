package no.nav.tjenestepensjon.simulering.domain;

public class TPOrdning {
    private String tssId;
    private String tpId;

    public TPOrdning() {
    }

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

    @Override
    public String toString() {
        return "TPOrdning{" +
                "tssId='" + tssId + '\'' +
                ", tpId='" + tpId + '\'' +
                '}';
    }
}
