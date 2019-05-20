package no.nav.tjenestepensjon.simulering.domain;

public class TPOrdning {
    private String tssId;
    private String tpId;
    private TpLeverandor tpLeverandor;

    public TPOrdning(){}

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

    public TpLeverandor getTpLeverandor() {
        return tpLeverandor;
    }

    public void setTpLeverandor(TpLeverandor tpLeverandor) {
        this.tpLeverandor = tpLeverandor;
    }
}
