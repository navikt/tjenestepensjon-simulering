package no.nav.tjenestepensjon.simulering.v2.exceptions

import no.nav.tjenestepensjon.simulering.exceptions.SimuleringException

class MissingOpptjeningsperiodeException(msg: String?) : SimuleringException("IKKE", msg)