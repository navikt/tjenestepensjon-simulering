package no.nav.tjenestepensjon.simulering.config

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDate

@Configuration
class ObjectMapperConfig {

    @Bean
    fun objectMapper(): ObjectMapper = ObjectMapper()
        .registerKotlinModule()
        .registerModule(
            JavaTimeModule()
                .addSerializer(LocalDate::class.java, LocalDateEpochSerializer())
        ).apply {
            enable(ACCEPT_CASE_INSENSITIVE_ENUMS)
        }


    class LocalDateEpochSerializer : JsonSerializer<LocalDate>() {
        override fun serialize(value: LocalDate, gen: JsonGenerator, serializers: SerializerProvider) {
            gen.writeString(value.toString())
        }
    }
}
