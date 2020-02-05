package no.nav.tjenestepensjon.simulering.domain

import java.time.LocalDate

interface Dateable: Comparable<Dateable> {
    var datoFom: LocalDate
    override operator fun compareTo(other: Dateable) = datoFom.compareTo(other.datoFom)
}