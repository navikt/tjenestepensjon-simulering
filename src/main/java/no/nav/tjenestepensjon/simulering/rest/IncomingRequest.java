package no.nav.tjenestepensjon.simulering.rest;

import java.util.Date;
import java.util.List;

import no.nav.tjenestepensjon.simulering.domain.Dateable;
import no.nav.tjenestepensjon.simulering.domain.DelytelseType;

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

    @Override
    public String toString() {
        return "IncomingRequest{" +
                "fnr='" + fnr + '\'' +
                ", sivilstandkode='" + sivilstandkode + '\'' +
                ", sprak='" + sprak + '\'' +
                ", simuleringsperioder=" + simuleringsperioder +
                ", simulertAFPOffentlig=" + simulertAFPOffentlig +
                ", simulertAFPPrivat=" + simulertAFPPrivat +
                ", pensjonsbeholdningsperioder=" + pensjonsbeholdningsperioder +
                ", inntekter=" + inntekter +
                '}';
    }

    public static class Simuleringsperiode implements Dateable {

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

        @Override
        public String toString() {
            return "Simuleringsperiode{" +
                    "datoFom=" + datoFom +
                    ", utg=" + utg +
                    ", stillingsprosentOffentlig=" + stillingsprosentOffentlig +
                    ", poengArTom1991=" + poengArTom1991 +
                    ", poengArFom1992=" + poengArFom1992 +
                    ", sluttpoengtall=" + sluttpoengtall +
                    ", anvendtTrygdetid=" + anvendtTrygdetid +
                    ", forholdstall=" + forholdstall +
                    ", delingstall=" + delingstall +
                    ", uforegradVedOmregning=" + uforegradVedOmregning +
                    ", delytelser=" + delytelser +
                    '}';
        }
    }

    public static class SimulerAfpPrivat {
        private Integer afpOpptjeningTotalbelop;
        private Double kompensasjonstillegg;

        public SimulerAfpPrivat(){}

        public SimulerAfpPrivat(Integer afpOpptjeningTotalbelop, Double kompensasjonstillegg) {
            this.afpOpptjeningTotalbelop = afpOpptjeningTotalbelop;
            this.kompensasjonstillegg = kompensasjonstillegg;
        }

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

        @Override
        public String toString() {
            return "SimulerAfpPrivat{" +
                    "afpOpptjeningTotalbelop=" + afpOpptjeningTotalbelop +
                    ", kompensasjonstillegg=" + kompensasjonstillegg +
                    '}';
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

        @Override
        public String toString() {
            return "Pensjonsbeholdningperiode{" +
                    "datoFom=" + datoFom +
                    ", pensjonsbeholdning=" + pensjonsbeholdning +
                    ", garantipensjonsbeholdning=" + garantipensjonsbeholdning +
                    ", garantilleggsbeholdning=" + garantilleggsbeholdning +
                    '}';
        }
    }

    public static class Inntekt implements Dateable {
        private Date datoFom;
        private Double inntekt;

        public Inntekt(){}

        public Inntekt(Date datoFom, Double inntekt) {
            this.datoFom = datoFom;
            this.inntekt = inntekt;
        }

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

        @Override
        public String toString() {
            return "Inntekt{" +
                    "datoFom=" + datoFom +
                    ", inntekt=" + inntekt +
                    '}';
        }
    }

    public static class Delytelse {

        private String pensjonstype;
        private Double belop;

        public Delytelse(){}

        public Delytelse(String pensjonstype, Double belop) {
            this.pensjonstype = pensjonstype;
            this.belop = belop;
        }

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

        public boolean hasPensjonstype(DelytelseType delytelseType) {
            return delytelseType.name().equalsIgnoreCase(getPensjonstype());
        }

        @Override
        public String toString() {
            return "Delytelse{" +
                    "pensjonstype='" + pensjonstype + '\'' +
                    ", belop=" + belop +
                    '}';
        }
    }
}
