package no.nav.tjenestepensjon.simulering.config

import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor.EndpointImpl
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TpLeverandorConfigOld {

    private lateinit var leverandorUrlMap: String
    @Value("\${TP_LEVERANDOR_URL_MAP_OLD}")
    fun setLeverandorUrlMap(leverandorUrlMap: String) {
        this.leverandorUrlMap = leverandorUrlMap
    }

    @Bean
    fun tpLeverandorListOld() = createListFromEnv(leverandorUrlMap)

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
        assert(details.size == 3)
        return TpLeverandor(details[0], details[1], EndpointImpl.valueOf(details[2]))
    }
}