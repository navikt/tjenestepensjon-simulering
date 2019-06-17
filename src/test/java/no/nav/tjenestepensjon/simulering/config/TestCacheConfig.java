package no.nav.tjenestepensjon.simulering.config;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import com.github.benmanes.caffeine.cache.Caffeine;

import org.springframework.cache.Cache;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
class TestCacheConfig {

    static final String TEST_CACHE = "TEST_CACHE";
    static final String TEST_CACHE_EXPIRES = "TEST_CACHE_EXPIRES";
    static final long SLEEP_TIME = 250L;

    @Bean
    public TestCacheable testCacheable() {
        return new TestCacheable();
    }

    @Bean
    public Cache testCache() {
        return new CaffeineCache(TEST_CACHE, Caffeine.newBuilder()
                .expireAfterWrite(10, SECONDS)
                .build());
    }

    @Bean
    public Cache testCacheExpires() {
        return new CaffeineCache(TEST_CACHE_EXPIRES, Caffeine.newBuilder()
                .expireAfterWrite(SLEEP_TIME, MILLISECONDS)
                .build());
    }

    static class TestCacheable {

        @Cacheable(value = TEST_CACHE)
        public String getCacheable(String input) {
            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "cached";
        }

        @Cacheable(value = TEST_CACHE_EXPIRES)
        public String getCacheableExpires(String input) {
            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "cached";
        }
    }
}