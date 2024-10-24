package no.nav.tjenestepensjon.simulering.config

import ch.qos.logback.access.common.spi.IAccessEvent
import com.fasterxml.jackson.core.JsonGenerator
import net.logstash.logback.composite.AbstractFieldJsonProvider
import net.logstash.logback.composite.JsonWritingUtils
import no.nav.tjenestepensjon.simulering.config.CorrelationIdFilter.Companion.CORRELATION_ID
import java.io.IOException
import java.util.*

class CorrelationIdProvider : AbstractFieldJsonProvider<IAccessEvent>() {
    init {
        fieldName = CORRELATION_ID
    }

    @Throws(IOException::class)
    override fun writeTo(generator: JsonGenerator, event: IAccessEvent) {
        JsonWritingUtils.writeStringField(generator, fieldName, Optional.ofNullable(event.getRequestHeader(CorrelationIdFilter.CORRELATION_ID_HTTP_HEADER)).orElseGet { UUID.randomUUID().toString() })
    }
}
