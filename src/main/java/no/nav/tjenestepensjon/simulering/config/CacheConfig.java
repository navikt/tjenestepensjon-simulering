package no.nav.tjenestepensjon.simulering.config;

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.HOURS;

import com.github.benmanes.caffeine.cache.Caffeine;

import org.springframework.cache.Cache;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

    public static final String TP_ORDNING_LEVERANDOR_CACHE = "TP_ORDNING_LEVERANDOR_CACHE";
    public static final String TP_ORDNING_PERSON_CACHE = "TP_ORDNING_PERSON_CACHE";

    @Bean
    public Cache tpOrdningLeverandorCache() {
        return new CaffeineCache(TP_ORDNING_LEVERANDOR_CACHE, Caffeine.newBuilder()
                .expireAfterWrite(1, DAYS)
                .build());
    }

    @Bean
    public Cache tpOrdningPersonCache() {
        return new CaffeineCache(TP_ORDNING_PERSON_CACHE, Caffeine.newBuilder()
                .expireAfterWrite(1, HOURS)
                .build());
    }
}
