package no.nav.tjenestepensjon.simulering.model.v1.request

import no.nav.tjenestepensjon.simulering.model.v1.domain.FNR
import no.nav.tjenestepensjon.simulering.model.v1.domain.Inntekt
import no.nav.tjenestepensjon.simulering.model.v1.domain.Pensjonsbeholdningperiode
import no.nav.tjenestepensjon.simulering.model.v1.domain.Simuleringsperiode

data class SimulerPensjonRequest(
        val fnr: FNR,
        val sivilstandkode: String,
        val sprak: String = "norsk",
        val simuleringsperioder: List<Simuleringsperiode>,
        val simulertAFPOffentlig: Int? = null,
        val simulertAFPPrivat: SimulertAFPPrivat? = null,
        val pensjonsbeholdningsperioder: List<Pensjonsbeholdningperiode> = emptyList(),
        val inntekter: List<Inntekt>
)