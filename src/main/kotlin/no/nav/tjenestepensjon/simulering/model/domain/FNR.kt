package no.nav.tjenestepensjon.simulering.model.domain

import com.fasterxml.jackson.annotation.JsonValue
import jakarta.xml.bind.annotation.XmlAccessType.FIELD
import jakarta.xml.bind.annotation.XmlAccessorType
import jakarta.xml.bind.annotation.XmlType

@XmlAccessorType(FIELD)
@XmlType(name = "", propOrder = ["fnr"])

data class FNR(
        @get:JsonValue val fnr: String
) {
    override fun toString() = fnr
}