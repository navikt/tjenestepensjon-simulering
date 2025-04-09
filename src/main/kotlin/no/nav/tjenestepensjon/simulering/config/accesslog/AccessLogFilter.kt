package no.nav.tjenestepensjon.simulering.config.accesslog

import ch.qos.logback.access.common.spi.AccessEvent
import ch.qos.logback.core.filter.EvaluatorFilter
import ch.qos.logback.core.spi.FilterReply

class AccessLogFilter : EvaluatorFilter<AccessEvent>() {

    override fun decide(e: AccessEvent) : FilterReply {
        return if (e.requestURI.contains("/actuator/")) {
            FilterReply.DENY
        } else {
            FilterReply.NEUTRAL
        }
    }
}