package no.nav.tjenestepensjon.simulering.rest;

import java.util.Date;
import java.util.List;

public class IncomingRequest {

    private String fnr;
    private String sivilstandkode;
    private String sprak;
    private List<Simuleringsperiode> simuleringsperioder;
    private Integer simulertAFPOffentlig;
    private SimulerAfpPrivat simulertAFPPrivat;
    private List<Pensjonsbeholdningperiode> pensjonsbeholdningsperioder;
    private List<Inntekt> inntekter;

    public String getFnr() {
        return fnr;
    }

    public void setFnr(String fnr) {
        this.fnr = fnr;
    }

    public String getSivilstandkode() {
        return sivilstandkode;
    }

    public void setSivilstandkode(String sivilstandkode) {
        this.sivilstandkode = sivilstandkode;
    }

    public String getSprak() {
        return sprak;
    }

    public void setSprak(String sprak) {
        this.sprak = sprak;
    }

    public List<Simuleringsperiode> getSimuleringsperioder() {
        return simuleringsperioder;
    }

    public void setSimuleringsperioder(List<Simuleringsperiode> simuleringsperioder) {
        this.simuleringsperioder = simuleringsperioder;
    }

    public Integer getSimulertAFPOffentlig() {
        return simulertAFPOffentlig;
    }

    public void setSimulertAFPOffentlig(Integer simulertAFPOffentlig) {
        this.simulertAFPOffentlig = simulertAFPOffentlig;
    }

    public SimulerAfpPrivat getSimulertAFPPrivat() {
        return simulertAFPPrivat;
    }

    public void setSimulertAFPPrivat(SimulerAfpPrivat simulertAFPPrivat) {
        this.simulertAFPPrivat = simulertAFPPrivat;
    }

    public List<Pensjonsbeholdningperiode> getPensjonsbeholdningsperioder() {
        return pensjonsbeholdningsperioder;
    }

    public void setPensjonsbeholdningsperioder(List<Pensjonsbeholdningperiode> pensjonsbeholdningsperioder) {
        this.pensjonsbeholdningsperioder = pensjonsbeholdningsperioder;
    }

    public List<Inntekt> getInntekter() {
        return inntekter;
    }

    public void setInntekter(List<Inntekt> inntekter) {
        this.inntekter = inntekter;
    }

    public static class Simuleringsperiode {

        private Date datoFom;
        private Integer utg;
        private Integer stillingsprosentOffentlig;
        private Integer poengArTom1991;
        private Integer poengArFom1992;
        private Double sluttpoengtall;
        private Integer anvendtTrygdetid;
        private Double forholdstall;
        private Double delingstall;
        private Integer uforegradVedOmregning;
        private List<Delytelse> delytelser;

        public Date getDatoFom() {
            return datoFom;
        }

        public void setDatoFom(Date datoFom) {
            this.datoFom = datoFom;
        }

        public Integer getUtg() {
            return utg;
        }

        public void setUtg(Integer utg) {
            this.utg = utg;
        }

        public Integer getStillingsprosentOffentlig() {
            return stillingsprosentOffentlig;
        }

        public void setStillingsprosentOffentlig(Integer stillingsprosentOffentlig) {
            this.stillingsprosentOffentlig = stillingsprosentOffentlig;
        }

        public Integer getPoengArTom1991() {
            return poengArTom1991;
        }

        public void setPoengArTom1991(Integer poengArTom1991) {
            this.poengArTom1991 = poengArTom1991;
        }

        public Integer getPoengArFom1992() {
            return poengArFom1992;
        }

        public void setPoengArFom1992(Integer poengArFom1992) {
            this.poengArFom1992 = poengArFom1992;
        }

        public Double getSluttpoengtall() {
            return sluttpoengtall;
        }

        public void setSluttpoengtall(Double sluttpoengtall) {
            this.sluttpoengtall = sluttpoengtall;
        }

        public Integer getAnvendtTrygdetid() {
            return anvendtTrygdetid;
        }

        public void setAnvendtTrygdetid(Integer anvendtTrygdetid) {
            this.anvendtTrygdetid = anvendtTrygdetid;
        }

        public Double getForholdstall() {
            return forholdstall;
        }

        public void setForholdstall(Double forholdstall) {
            this.forholdstall = forholdstall;
        }

        public Double getDelingstall() {
            return delingstall;
        }

        public void setDelingstall(Double delingstall) {
            this.delingstall = delingstall;
        }

        public Integer getUforegradVedOmregning() {
            return uforegradVedOmregning;
        }

        public void setUforegradVedOmregning(Integer uforegradVedOmregning) {
            this.uforegradVedOmregning = uforegradVedOmregning;
        }

        public List<Delytelse> getDelytelser() {
            return delytelser;
        }

        public void setDelytelser(List<Delytelse> delytelser) {
            this.delytelser = delytelser;
        }
    }

    public static class SimulerAfpPrivat {
        private Integer afpOpptjeningTotalbelop;
        private Double kompensasjonstillegg;

        public Integer getAfpOpptjeningTotalbelop() {
            return afpOpptjeningTotalbelop;
        }

        public void setAfpOpptjeningTotalbelop(Integer afpOpptjeningTotalbelop) {
            this.afpOpptjeningTotalbelop = afpOpptjeningTotalbelop;
        }

        public Double getKompensasjonstillegg() {
            return kompensasjonstillegg;
        }

        public void setKompensasjonstillegg(Double kompensasjonstillegg) {
            this.kompensasjonstillegg = kompensasjonstillegg;
        }
    }

    public static class Pensjonsbeholdningperiode {
        private Date datoFom;
        private Integer pensjonsbeholdning;
        private Integer garantipensjonsbeholdning;
        private Integer garantilleggsbeholdning;

        public Date getDatoFom() {
            return datoFom;
        }

        public void setDatoFom(Date datoFom) {
            this.datoFom = datoFom;
        }

        public Integer getPensjonsbeholdning() {
            return pensjonsbeholdning;
        }

        public void setPensjonsbeholdning(Integer pensjonsbeholdning) {
            this.pensjonsbeholdning = pensjonsbeholdning;
        }

        public Integer getGarantipensjonsbeholdning() {
            return garantipensjonsbeholdning;
        }

        public void setGarantipensjonsbeholdning(Integer garantipensjonsbeholdning) {
            this.garantipensjonsbeholdning = garantipensjonsbeholdning;
        }

        public Integer getGarantilleggsbeholdning() {
            return garantilleggsbeholdning;
        }

        public void setGarantilleggsbeholdning(Integer garantilleggsbeholdning) {
            this.garantilleggsbeholdning = garantilleggsbeholdning;
        }
    }

    public static class Inntekt {
        private Date datoFom;
        private Double inntekt;

        public Date getDatoFom() {
            return datoFom;
        }

        public void setDatoFom(Date datoFom) {
            this.datoFom = datoFom;
        }

        public Double getInntekt() {
            return inntekt;
        }

        public void setInntekt(Double inntekt) {
            this.inntekt = inntekt;
        }
    }


    public static class Delytelse {

        private String pensjonstype;
        private Double belop;

        public String getPensjonstype() {
            return pensjonstype;
        }

        public void setPensjonstype(String pensjonstype) {
            this.pensjonstype = pensjonstype;
        }

        public Double getBelop() {
            return belop;
        }

        public void setBelop(Double belop) {
            this.belop = belop;
        }
    }

}