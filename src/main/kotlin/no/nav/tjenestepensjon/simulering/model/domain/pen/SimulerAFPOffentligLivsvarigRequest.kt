package no.nav.tjenestepensjon.simulering.model.domain.pen

import no.nav.tjenestepensjon.simulering.v2.models.domain.SivilstandCodeEnum
import java.time.LocalDate

data class SimulerAFPOffentligLivsvarigRequest(val fnr: String, val fodselsdato: LocalDate, val fremtidigeInntekter: List<FremtidigInntekt>,
                                               val sivilstandVedPensjonering: SivilstandCodeEnum, val fom: LocalDate,
                                               val arIUtlandetEtter16: Int, val epsPensjon: Boolean, val eps2G: Boolean
    )
