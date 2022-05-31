package no.nav.tjenestepensjon.simulering.config

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDate

@Configuration
class ObjectMapperConfig {

    @Bean
    fun objectMapper() = jsonMapper {
        enable(ACCEPT_CASE_INSENSITIVE_ENUMS)
        addModules(
            kotlinModule(),
            JavaTimeModule().addSerializer(LocalDate::class.java, LocalDateEpochSerializer())
        )
    }


    class LocalDateEpochSerializer : JsonSerializer<LocalDate>() {
        override fun serialize(value: LocalDate, gen: JsonGenerator, serializers: SerializerProvider) {
            gen.writeString(value.toString())
        }
    }
}
