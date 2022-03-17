package no.nav.tjenestepensjon.simulering.config

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.addSerializer
import com.fasterxml.jackson.module.kotlin.kotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDate

@Configuration
class JsonMapperConfig {

    @Bean
    fun jsonMapper(): JsonMapper = JsonMapper.builder()
        .enable(ACCEPT_CASE_INSENSITIVE_ENUMS)
        .addModules(
            localDateEpochModule(),
            kotlinModule()
        ).build()

    private fun localDateEpochModule() = JavaTimeModule().addSerializer(LocalDate::class, LocalDateEpochSerializer())

    class LocalDateEpochSerializer : JsonSerializer<LocalDate>() {
        override fun serialize(value: LocalDate, gen: JsonGenerator, serializers: SerializerProvider) {
            gen.writeString(value.toString())
        }
    }
}
