package no.nav.tjenestepensjon.simulering.rest;

import java.util.Date;
import java.util.List;

public class OutgoingResponse {
    private List<SimulertPensjon> simulertPensjonListe;

    public List<SimulertPensjon> getSimulertPensjonListe() {
        return simulertPensjonListe;
    }

    public void setSimulertPensjonListe(List<SimulertPensjon> simulertPensjonListe) {
        this.simulertPensjonListe = simulertPensjonListe;
    }

    @Override
    public String toString() {
        return "OutgoingResponse{" +
                "simulertPensjonListe=" + simulertPensjonListe +
                '}';
    }

    public static class SimulertPensjon {

        private String tpnr;
        private String navnOrdning;
        private List<String> inkluderteOrdninger;
        private String leverandorUrl;
        private List<String> inkluderteTpnr;
        private List<String> utelatteTpnr;
        private String status;
        private String feilkode;
        private String feilbeskrivelse;
        private List<Utbetalingsperiode> utbetalingsperioder;

        public String getTpnr() {
            return tpnr;
        }

        public void setTpnr(String tpnr) {
            this.tpnr = tpnr;
        }

        public String getNavnOrdning() {
            return navnOrdning;
        }

        public void setNavnOrdning(String navnOrdning) {
            this.navnOrdning = navnOrdning;
        }

        public List<String> getInkluderteOrdninger() {
            return inkluderteOrdninger;
        }

        public void setInkluderteOrdninger(List<String> inkluderteOrdninger) {
            this.inkluderteOrdninger = inkluderteOrdninger;
        }

        public String getLeverandorUrl() {
            return leverandorUrl;
        }

        public void setLeverandorUrl(String leverandorUrl) {
            this.leverandorUrl = leverandorUrl;
        }

        public List<String> getInkluderteTpnr() {
            return inkluderteTpnr;
        }

        public void setInkluderteTpnr(List<String> inkluderteTpnr) {
            this.inkluderteTpnr = inkluderteTpnr;
        }

        public List<String> getUtelatteTpnr() {
            return utelatteTpnr;
        }

        public void setUtelatteTpnr(List<String> utelatteTpnr) {
            this.utelatteTpnr = utelatteTpnr;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getFeilkode() {
            return feilkode;
        }

        public void setFeilkode(String feilkode) {
            this.feilkode = feilkode;
        }

        public String getFeilbeskrivelse() {
            return feilbeskrivelse;
        }

        public void setFeilbeskrivelse(String feilbeskrivelse) {
            this.feilbeskrivelse = feilbeskrivelse;
        }

        public List<Utbetalingsperiode> getUtbetalingsperioder() {
            return utbetalingsperioder;
        }

        public void setUtbetalingsperioder(List<Utbetalingsperiode> utbetalingsperioder) {
            this.utbetalingsperioder = utbetalingsperioder;
        }

        @Override
        public String toString() {
            return "SimulertPensjon{" +
                    "tpnr='" + tpnr + '\'' +
                    ", navnOrdning='" + navnOrdning + '\'' +
                    ", inkluderteOrdninger=" + inkluderteOrdninger +
                    ", leverandorUrl='" + leverandorUrl + '\'' +
                    ", inkluderteTpnr=" + inkluderteTpnr +
                    ", utelatteTpnr=" + utelatteTpnr +
                    ", status='" + status + '\'' +
                    ", feilkode='" + feilkode + '\'' +
                    ", feilbeskrivelse='" + feilbeskrivelse + '\'' +
                    ", utbetalingsperioder=" + utbetalingsperioder +
                    '}';
        }
    }

    public static class Utbetalingsperiode {
        private Date datoFom;
        private Date datoTom;
        private Integer grad;
        private Double arligUtbetaling;
        private String ytelsekode;
        private String mangelfullSimuleringkode;

        public Date getDatoFom() {
            return datoFom;
        }

        public void setDatoFom(Date datoFom) {
            this.datoFom = datoFom;
        }

        public Date getDatoTom() {
            return datoTom;
        }

        public void setDatoTom(Date datoTom) {
            this.datoTom = datoTom;
        }

        public Integer getGrad() {
            return grad;
        }

        public void setGrad(Integer grad) {
            this.grad = grad;
        }

        public Double getArligUtbetaling() {
            return arligUtbetaling;
        }

        public void setArligUtbetaling(Double arligUtbetaling) {
            this.arligUtbetaling = arligUtbetaling;
        }

        public String getYtelsekode() {
            return ytelsekode;
        }

        public void setYtelsekode(String ytelsekode) {
            this.ytelsekode = ytelsekode;
        }

        public String getMangelfullSimuleringkode() {
            return mangelfullSimuleringkode;
        }

        public void setMangelfullSimuleringkode(String mangelfullSimuleringkode) {
            this.mangelfullSimuleringkode = mangelfullSimuleringkode;
        }

        @Override
        public String toString() {
            return "Utbetalingsperiode{" +
                    "datoFom=" + datoFom +
                    ", datoTom=" + datoTom +
                    ", grad=" + grad +
                    ", arligUtbetaling=" + arligUtbetaling +
                    ", ytelsekode='" + ytelsekode + '\'' +
                    ", mangelfullSimuleringkode='" + mangelfullSimuleringkode + '\'' +
                    '}';
        }
    }

}