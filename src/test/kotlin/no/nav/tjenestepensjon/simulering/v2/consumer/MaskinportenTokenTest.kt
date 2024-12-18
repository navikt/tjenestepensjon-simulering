package no.nav.tjenestepensjon.simulering.v2.consumer

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.web.reactive.function.client.WebClient

@ExtendWith(MockitoExtension::class)
class MaskinportenTokenTest{

    @Mock
    private lateinit var webClient: WebClient

    private lateinit var maskinportenClient: MaskinportenToken

    private var fetchTokenCallCount = 0

    @BeforeEach
    fun setUp() {
        // Set up the WebClient mock to return a MaskinportenToken
        maskinportenClient = object : MaskinportenToken(webClient, "clientId", "clientJwk", "issuer", "endpoint") {
            override fun fetchToken(scope: String): String {
                // Return a mock token instead of performing real signing
                fetchTokenCallCount++
                return when (scope) {
                    "test-scope" -> "test-token"
                    "another-scope" -> "another-token"
                    else -> throw RuntimeException("Unexpected scope: $scope")
                }
            }
        }
    }

    @Test
    fun `should fetch token from cache when available`() {
        // First call, fetchToken should be called
        val countBeforeTestStart = fetchTokenCallCount
        val token1 = maskinportenClient.getToken("test-scope")
        assertEquals("test-token", token1)

        // Second call with the same scope, token should be retrieved from cache
        val token2 = maskinportenClient.getToken("test-scope")
        assertEquals("test-token", token2)

        // Assert that fetchToken was called only once
        assertEquals(
            countBeforeTestStart + 1,
            fetchTokenCallCount,
            "fetchToken should be called only once for 'test-scope'"
        )
    }

    @Test
    fun `should fetch new token when scope is different`() {
        // First call for "test-scope"
        val countBeforeTestStart = fetchTokenCallCount
        maskinportenClient.getToken("test-scope")

        // Call for a different scope, fetchToken should be called again
        val token = maskinportenClient.getToken("another-scope")
        assertEquals("another-token", token)

        // Assert that fetchToken was called twice in total (once for each scope)
        assertEquals(countBeforeTestStart + 2, fetchTokenCallCount, "fetchToken should be called once per unique scope")
    }
}