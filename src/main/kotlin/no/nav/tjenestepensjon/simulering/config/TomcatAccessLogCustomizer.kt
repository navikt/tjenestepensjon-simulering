package no.nav.tjenestepensjon.simulering.config

import ch.qos.logback.access.tomcat.LogbackValve
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.stereotype.Component

@Component
class TomcatAccessLogCustomizer : WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
    override fun customize(factory: TomcatServletWebServerFactory) {
        val logbackValve = LogbackValve()
        logbackValve.name = "Logback Access"
        logbackValve.filename = "logback-access.xml"
        factory.addContextValves(logbackValve)
    }
}