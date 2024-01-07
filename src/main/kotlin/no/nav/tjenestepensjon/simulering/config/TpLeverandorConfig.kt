package no.nav.tjenestepensjon.simulering.config

import no.nav.tjenestepensjon.simulering.model.domain.TpLeverandor
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.Binder
import org.springframework.boot.context.properties.bind.ConstructorBinding
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.core.env.Environment

@Configuration
class TpLeverandorConfig(private val environment: Environment) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Scope("singleton")
    @Bean("tpLeverandor")
    fun tpLeverandorList(): List<TpLeverandor> = PropertiesLoader(environment).let { propertiesLoader ->
        val list: MutableList<TpLeverandor> = mutableListOf()
        propertiesLoader.loadProperties("spk")?.let { list.add(it) }
        propertiesLoader.loadProperties("klp")?.let { list.add(it) }
        propertiesLoader.loadProperties("ofp")?.let { list.add(it) }
        propertiesLoader.loadProperties("gabler")?.let { list.add(it) }
        propertiesLoader.loadProperties("storebrand")?.let { list.add(it) }
        log.info("Loaded tp-leverandører: $list")
        return@let list.toList()
    }

    class PropertiesLoader(private val environment: Environment) {

        fun loadProperties(prefix: String): TpLeverandor? {
            return Binder.get(environment)
                .bind(prefix, TpLeverandorProperty::class.java)
                .map { TpLeverandor(it.name!!, it.implementation!!, it.simuleringUrl!!, it.stillingsprosentUrl!!) }
                .orElse(null)
        }
    }

    @ConfigurationProperties
    data class TpLeverandorProperty @ConstructorBinding constructor(
        val name: String?,
        val implementation: TpLeverandor.EndpointImpl?,
        val simuleringUrl: String?,
        val stillingsprosentUrl: String?
    )
}