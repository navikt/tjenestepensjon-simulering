package no.nav.tjenestepensjon.simulering.domain;

import java.time.LocalDate;

public class Stillingsprosent {

    protected Double stillingsprosent;
    protected LocalDate datoFom;
    protected LocalDate datoTom;
    protected String faktiskHovedlonn;
    protected String stillingsuavhengigTilleggslonn;
    protected Integer aldersgrense;

    public Stillingsprosent() { }

    public Double getStillingsprosent() {
        return stillingsprosent;
    }

    public void setStillingsprosent(Double stillingsprosent) {
        this.stillingsprosent = stillingsprosent;
    }

    public LocalDate getDatoFom() {
        return datoFom;
    }

    public void setDatoFom(LocalDate datoFom) {
        this.datoFom = datoFom;
    }

    public LocalDate getDatoTom() {
        return datoTom;
    }

    public void setDatoTom(LocalDate datoTom) {
        this.datoTom = datoTom;
    }

    public String getFaktiskHovedlonn() {
        return faktiskHovedlonn;
    }

    public void setFaktiskHovedlonn(String faktiskHovedlonn) {
        this.faktiskHovedlonn = faktiskHovedlonn;
    }

    public String getStillingsuavhengigTilleggslonn() {
        return stillingsuavhengigTilleggslonn;
    }

    public void setStillingsuavhengigTilleggslonn(String stillingsuavhengigTilleggslonn) {
        this.stillingsuavhengigTilleggslonn = stillingsuavhengigTilleggslonn;
    }

    public Integer getAldersgrense() {
        return aldersgrense;
    }

    public void setAldersgrense(Integer aldersgrense) {
        this.aldersgrense = aldersgrense;
    }

    @Override
    public String toString() {
        return "Stillingsprosent{" +
                "stillingsprosent=" + stillingsprosent +
                ", datoFom=" + datoFom +
                ", datoTom=" + datoTom +
                ", faktiskHovedlonn='" + faktiskHovedlonn + '\'' +
                ", stillingsuavhengigTilleggslonn='" + stillingsuavhengigTilleggslonn + '\'' +
                ", aldersgrense=" + aldersgrense +
                '}';
    }
}
