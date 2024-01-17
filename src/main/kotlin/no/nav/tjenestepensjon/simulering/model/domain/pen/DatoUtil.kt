package no.nav.tjenestepensjon.simulering.model.domain.pen

import java.time.format.DateTimeFormatter
import java.util.*

object DatoUtil {

    fun Date.datoToString(): String? {
        return DateTimeFormatter.ISO_LOCAL_DATE.format(this.toInstant())
    }
}