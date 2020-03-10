package no.nav.tjenestepensjon.simulering.v2.models.request

import no.nav.tjenestepensjon.simulering.domain.Dateable
import java.time.LocalDate

class Simuleringsperiode(
        override var datoFom: LocalDate,
        var folketrygdUttaksgrad: Int,
        var stillingsprosentOffentlig: Int,
        var simulerAFPOffentligEtterfulgtAvAlderListe: Boolean
) : Dateable