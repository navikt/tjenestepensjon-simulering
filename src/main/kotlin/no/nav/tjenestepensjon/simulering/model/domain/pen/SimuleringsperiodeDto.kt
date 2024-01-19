package no.nav.tjenestepensjon.simulering.model.domain.pen

import java.util.*

data class SimuleringsperiodeDto (
    val datoFom: Date,
    val folketrygdUttaksgrad: Int,
    val stillingsprosentOffentlig: Int,
    val simulerAFPOffentligEtterfulgtAvAlder: Boolean,
)
