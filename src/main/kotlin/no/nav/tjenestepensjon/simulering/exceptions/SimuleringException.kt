package no.nav.tjenestepensjon.simulering.exceptions

abstract class SimuleringException(val feilkode: String?, msg: String?) : Throwable(msg)