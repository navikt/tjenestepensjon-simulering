package no.nav.tjenestepensjon.simulering.v2.exceptions

abstract class MaskinportenException(val feilkode: String?, msg: String?) : Throwable(msg)