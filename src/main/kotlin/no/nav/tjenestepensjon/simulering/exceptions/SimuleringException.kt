package no.nav.tjenestepensjon.simulering.exceptions

import java.lang.RuntimeException

abstract class SimuleringException(val feilkode: String?, msg: String?) : RuntimeException(msg)