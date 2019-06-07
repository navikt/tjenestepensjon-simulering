package no.nav.tjenestepensjon.simulering.domain;

import java.util.ArrayList;
import java.util.List;

public class TPOrdning {
    private String tssId;
    private String tpId;
    private TpLeverandor tpLeverandor;
    private List<Stillingsprosent> stillingsprosentList = new ArrayList<>();

    public TPOrdning(){}

    public TPOrdning(String tssId, String tpId) {
        this.tssId = tssId;
        this.tpId = tpId;
    }

    public TPOrdning(String tssId, String tpId, TpLeverandor tpLeverandor){
        this(tssId, tpId);
        this.tpLeverandor = tpLeverandor;
    }

    public TPOrdning(String tssId, String tpId, TpLeverandor tpLeverandor, List<Stillingsprosent> stillingsprosentList){
        this(tssId, tpId);
        this.tpLeverandor = tpLeverandor;
        this.stillingsprosentList = stillingsprosentList;
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

    public List<Stillingsprosent> getStillingsprosentList() {
        return stillingsprosentList;
    }

    public void setStillingsprosentList(List<Stillingsprosent> stillingsprosentList) {
        this.stillingsprosentList = stillingsprosentList;
    }

    @Override
    public String toString() {
        return "TPOrdning{" +
                "tssId='" + tssId + '\'' +
                ", tpId='" + tpId + '\'' +
                ", tpLeverandor=" + tpLeverandor +
                ", stillingsprosentList=" + stillingsprosentList +
                '}';
    }
}
