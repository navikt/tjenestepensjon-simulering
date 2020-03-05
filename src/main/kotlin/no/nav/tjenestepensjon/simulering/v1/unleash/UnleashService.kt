package no.nav.tjenestepensjon.simulering.v1.unleash

import no.finn.unleash.DefaultUnleash
import no.finn.unleash.Unleash
import no.finn.unleash.util.UnleashConfig
import org.springframework.stereotype.Service


@Service
class UnleashService internal constructor() {
    val unleash: Unleash = DefaultUnleash(
            UnleashConfig.builder()
                    .appName("java-test")
                    .instanceId("instance x")
                    .unleashAPI("http://unleash.herokuapp.com/api/")
                    .build()
    )
    val unleashNewModelEnabled: String = "pesys.pen.tp-simulering"

    fun isNewModelEnabled() = unleash.isEnabled(unleashNewModelEnabled, true)
}