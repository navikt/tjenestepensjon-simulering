package no.nav.tjenestepensjon.simulering.v1.models.request

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import no.nav.tjenestepensjon.simulering.model.domain.FNR
import no.nav.tjenestepensjon.simulering.v1.models.domain.Inntekt
import no.nav.tjenestepensjon.simulering.model.domain.Pensjonsbeholdningsperiode
import no.nav.tjenestepensjon.simulering.v1.models.domain.Simuleringsperiode

@JsonIgnoreProperties(ignoreUnknown = true)
data class SimulerPensjonRequestV1(
    val fnr: FNR,
    val sivilstandkode: String,
    val sprak: String? = "norsk",
    val simuleringsperioder: List<Simuleringsperiode>,
    val simulertAFPOffentlig: Int? = null,
    val simulertAFPPrivat: SimulertAFPPrivat? = null,
    val pensjonsbeholdningsperioder: List<Pensjonsbeholdningsperiode> = emptyList(),
    val inntekter: List<Inntekt>
)
