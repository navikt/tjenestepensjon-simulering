package no.nav.tjenestepensjon.simulering.model.domain

import no.nav.tjenestepensjon.simulering.model.domain.pen.AlderForDelingstall

data class PensjonsbeholdningMedDelingstall(val pensjonsbeholdning: Int, val delingstall: Double, val alderForDelingstall: AlderForDelingstall)
