package no.nav.tjenestepensjon.simulering.model.v1.request

import com.fasterxml.jackson.annotation.JsonCreator
import no.nav.tjenestepensjon.simulering.model.v1.domain.FNR
import no.nav.tjenestepensjon.simulering.model.v1.domain.Inntekt
import no.nav.tjenestepensjon.simulering.model.v1.domain.Pensjonsbeholdningperiode
import no.nav.tjenestepensjon.simulering.model.v1.domain.Simuleringsperiode

data class SimulerPensjonRequest @JsonCreator constructor(
        var fnr: FNR,
        var sivilstandkode: String,
        var sprak: String = "norsk",
        var simuleringsperioder: List<Simuleringsperiode>,
        var simulertAFPOffentlig: Int? = null,
        var simulertAFPPrivat: SimulertAFPPrivat? = null,
        var pensjonsbeholdningsperioder: List<Pensjonsbeholdningperiode> = emptyList(),
        var inntekter: List<Inntekt>
)