package no.nav.tjenestepensjon.simulering.config

import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.TjenestepensjonV2025Client
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.klp.KLPTjenestepensjonClient
import no.nav.tjenestepensjon.simulering.v2025.tjenestepensjon.v1.service.spk.SPKTjenestepensjonClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class Tjenestepensjon2025ClientConfig {

    //TODO konfigurer KLP og SPK clients - implementeres i PEK-503 PEK-504
    @Bean("klp")
    fun klpTjenestepensjonV2025Client(): TjenestepensjonV2025Client {
        return KLPTjenestepensjonClient()
    }

    @Bean("spk")
    fun spkTjenestepensjonV2025Client(): TjenestepensjonV2025Client {
        return SPKTjenestepensjonClient()
    }
}