package no.nav.tjenestepensjon.simulering.v2.models.request

class Simuleringsdata(
        val datoFom: String,
        val andvendtTrygdetid: Int,
        val poengArTom1991: Int,
        val poengArFom1992: Int,
        val uforegradVedOmregning: Int?,
        val basisgp: Double? ,
        val basispt: Double?,
        val basistp: Double?,
        val delingstallUttak: Double,
        val forholdstallUttak: Double,
        val sluttpoengtall: Double?
)