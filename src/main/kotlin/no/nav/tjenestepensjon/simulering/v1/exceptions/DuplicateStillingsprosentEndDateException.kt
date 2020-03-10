package no.nav.tjenestepensjon.simulering.v1.exceptions

import no.nav.tjenestepensjon.simulering.exceptions.SimuleringException

class DuplicateStillingsprosentEndDateException(msg: String?) : SimuleringException("PARF", msg)