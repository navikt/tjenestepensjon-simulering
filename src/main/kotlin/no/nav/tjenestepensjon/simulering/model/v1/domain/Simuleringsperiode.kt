package no.nav.tjenestepensjon.simulering.model.v1.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import no.nav.tjenestepensjon.simulering.domain.Dateable
import no.nav.tjenestepensjon.simulering.domain.DelytelseType
import java.time.LocalDate

data class Simuleringsperiode(
        override var datoFom: LocalDate,
        var utg: Int,
        var stillingsprosentOffentlig: Int,
        var poengArTom1991: Int,
        var poengArFom1992: Int,
        var sluttpoengtall: Double,
        var anvendtTrygdetid: Int,
        var forholdstall: Double,
        var delingstall: Double,
        var uforegradVedOmregning: Int,
        var delytelser: List<Delytelse>
) : Dateable {
    @JsonIgnore
    fun isGradert() = utg < 100

    fun getDelytelseBelop(delytelseType: DelytelseType) =
            delytelser.firstOrNull { delytelse: Delytelse -> delytelse.pensjonstype == delytelseType }?.belop
}