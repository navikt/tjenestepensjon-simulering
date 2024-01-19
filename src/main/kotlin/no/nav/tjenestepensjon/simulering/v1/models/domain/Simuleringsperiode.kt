package no.nav.tjenestepensjon.simulering.v1.models.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import no.nav.tjenestepensjon.simulering.domain.Dateable
import no.nav.tjenestepensjon.simulering.domain.DelytelseType
import java.time.LocalDate

data class Simuleringsperiode(
        override var datoFom: LocalDate,
        var utg: Int,
        var stillingsprosentOffentlig: Int,
) : Dateable {
        var poengArTom1991: Int? = null
        var poengArFom1992: Int? = null
        var sluttpoengtall: Double? = null
        var anvendtTrygdetid: Int? = null
        var forholdstall: Double? = null
        var delingstall: Double? = null
        var uforegradVedOmregning: Int? = null
        var delytelser: List<Delytelse> = emptyList()
    @JsonIgnore
    fun isGradert() = utg < 100

    fun getDelytelseBelop(delytelseType: DelytelseType) =
            delytelser.firstOrNull { delytelse: Delytelse -> delytelse.pensjonstype == delytelseType }?.belop
}