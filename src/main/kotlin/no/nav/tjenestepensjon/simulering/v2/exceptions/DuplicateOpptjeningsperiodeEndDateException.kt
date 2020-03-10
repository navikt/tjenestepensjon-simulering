package no.nav.tjenestepensjon.simulering.v2.exceptions

import no.nav.tjenestepensjon.simulering.exceptions.SimuleringException

class DuplicateOpptjeningsperiodeEndDateException(msg: String?) : SimuleringException("PARF", msg)