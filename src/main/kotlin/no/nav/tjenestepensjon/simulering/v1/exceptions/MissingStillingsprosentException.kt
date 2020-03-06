package no.nav.tjenestepensjon.simulering.v1.exceptions

import no.nav.tjenestepensjon.simulering.exceptions.SimuleringException

class MissingStillingsprosentException(msg: String?) : SimuleringException("IKKE", msg)