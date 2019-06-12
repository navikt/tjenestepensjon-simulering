package no.nav.tjenestepensjon.simulering.config;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import static no.nav.tjenestepensjon.simulering.config.CacheConfigTest.TestCacheConfig;

import java.util.List;

import com.github.benmanes.caffeine.cache.Caffeine;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootTest(classes = TestCacheConfig.class)
class CacheConfigTest {

    private static final String TEST_CACHE = "TEST_CACHE";
    private static final String TEST_CACHE_EXPIRES = "TEST_CACHE_EXPIRES";
    private static final long SLEEP_TIME = 250L;

    @Autowired
    private CacheManager testCacheManager;
    @Autowired
    private TestCacheable testCacheable;

    @Test
    void shouldCacheResponses() {
        String input = "input";
        assertThat(testCacheManager.getCache(TEST_CACHE), is(notNullValue()));
        assertThat(testCacheManager.getCache(TEST_CACHE).get(input, String.class), is(nullValue()));

        long startTime = System.currentTimeMillis();
        String value = testCacheable.getCacheable(input);
        long endTime = System.currentTimeMillis() - startTime;
        String valueFromCache = testCacheManager.getCache(TEST_CACHE).get(input, String.class);

        assertThat(endTime > SLEEP_TIME, is(true));
        assertThat(valueFromCache, is(equalTo(value)));
        assertThat(valueFromCache, is("cached"));

        startTime = System.currentTimeMillis();
        String cached = testCacheable.getCacheable(input);
        endTime = System.currentTimeMillis() - startTime;
        assertThat(endTime < SLEEP_TIME, is(true));
        assertThat(cached, is("cached"));
    }

    @Test
    void shouldExpire() throws InterruptedException {
        String input = "input";
        assertThat(testCacheManager.getCache(TEST_CACHE_EXPIRES).get(input, String.class), is(nullValue()));
        String cached = testCacheable.getCacheableExpires(input);
        assertThat(testCacheManager.getCache(TEST_CACHE_EXPIRES).get(input, String.class), is(equalTo(cached)));
        Thread.sleep(SLEEP_TIME);
        assertThat(testCacheManager.getCache(TEST_CACHE_EXPIRES).get(input, String.class), is(nullValue()));
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

    @Configuration
    @EnableCaching
    static class TestCacheConfig {

        @Bean
        public TestCacheable testCacheable() {
            return new TestCacheable();
        }

        @Bean
        public CacheManager testCacheManager() {
            SimpleCacheManager simpleCacheManager = new SimpleCacheManager();
            simpleCacheManager.setCaches(List.of(testCache(), testCacheExpires()));
            return simpleCacheManager;
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
    }
}