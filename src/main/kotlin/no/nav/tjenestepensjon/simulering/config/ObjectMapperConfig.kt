package no.nav.tjenestepensjon.simulering.config

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDate

@Configuration
class ObjectMapperConfig {

    @Bean
    fun objectMapper(): ObjectMapper = ObjectMapper()
            .registerModule(KotlinModule())
            .registerModule(
                    JavaTimeModule()
                            .addSerializer(LocalDate::class.java, LocalDateEpochSerializer())
            )


    class LocalDateEpochSerializer : JsonSerializer<LocalDate>() {
        override fun serialize(value: LocalDate, gen: JsonGenerator, serializers: SerializerProvider) {
            gen.writeString(value.toString())
        }
    }
}
