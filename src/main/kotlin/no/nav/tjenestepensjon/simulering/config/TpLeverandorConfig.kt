package no.nav.tjenestepensjon.simulering.config

import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor.EndpointImpl
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TpLeverandorConfig {

    private lateinit var leverandorUrlMap: String
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
        assert(details.size == 4)
        return TpLeverandor(details[0], EndpointImpl.valueOf(details[1]), details[2], details[3])
    }
}