package no.nav.tjenestepensjon.simulering.model.domain

import no.nav.tjenestepensjon.simulering.model.domain.pen.AlderForDelingstall

data class AfpBeregningsgrunnlag(val pensjonsbeholdning: Int, val alderForDelingstall: AlderForDelingstall, val delingstall: Double)
