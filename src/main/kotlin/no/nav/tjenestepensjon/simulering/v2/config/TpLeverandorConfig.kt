package no.nav.tjenestepensjon.simulering.v2.config

import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TpLeverandorConfig {

    private lateinit var leverandorUrlMap: String

    private val maskinportenIntegrasjon = "maskinportenIntegrasjon"

    @Value("\${TP_LEVERANDOR_URL_MAP}")
    fun setLeverandorUrlMap(leverandorUrlMap: String) {
        this.leverandorUrlMap = leverandorUrlMap
    }

    @Bean("tpLeverandor")
    fun tpLeverandorList() = createListFromEnv(leverandorUrlMap)


    /**
     * Parse env variable to generate a list of TpLeverandor.
     * "," delimits the details of induvidual providers
     * "|" delimits different providers
     *
     * @param leverandorUrlMap env variable format "LEVERANDOR,URL,IMPL|..."
     * @return List of TpLeverandor
     */
    private fun createListFromEnv(leverandorUrlMap: String) =
            leverandorUrlMap.split('|').map(this::parseProvider)

    private fun parseProvider(provider: String): TpLeverandor {
        val details = provider.split(',')
        if (details.size == 2) { // not having maskinporten integrasion
            return TpLeverandor(details[0], details[1], null)
        } else if(details.size == 3 && details[2].equals(maskinportenIntegrasjon)) {
            return TpLeverandor(details[0], details[1], null, true)
        }

        throw AssertionError("provider does not contain the correct syntax: ${provider}")
    }
}