package no.nav.tjenestepensjon.simulering.v2.models.request

import no.nav.tjenestepensjon.simulering.domain.Dateable
import java.time.LocalDate

data class Simuleringsperiode(
        override var datoFom: LocalDate,
        var folketrygdUttaksgrad: Int,
        var stillingsprosentOffentlig: Int,
        var simulerAFPOffentligEtterfulgtAvAlder: Boolean
) : Dateable