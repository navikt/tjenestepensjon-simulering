package no.nav.tjenestepensjon.simulering.service

import io.getunleash.Unleash
import org.springframework.stereotype.Service

@Service
class FeatureToggleServiceImpl(private val unleash: Unleash) : FeatureToggleService {

    override fun isEnabled(featureName: String) = unleash.isEnabled(featureName)
}