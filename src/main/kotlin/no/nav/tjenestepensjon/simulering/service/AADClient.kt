package no.nav.tjenestepensjon.simulering.service

import com.microsoft.aad.msal4j.ClientCredentialFactory.createFromSecret
import com.microsoft.aad.msal4j.ClientCredentialParameters
import com.microsoft.aad.msal4j.ConfidentialClientApplication
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class AADClient(
    @Value("\${azure.app.client.id}") clientId: String,
    @Value("\${azure.app.client.secret}") clientSecret: String,
    @Value("\${azure.app.well-known-url}") authority: String
) {
    private val log = KotlinLogging.logger {}

    init {
        log.info { "Building AAD client with authority: $authority" }
    }

    private val app =
        ConfidentialClientApplication.builder(clientId, createFromSecret(clientSecret)).authority(authority).build()

    fun getToken(vararg scope: String): String {
        log.debug { "Fetching AAD token with scopes: ${scope.toList()}" }
        return app.acquireToken(withScopes(*scope)).get().accessToken()
    }

    private fun withScopes(vararg scope: String) = ClientCredentialParameters.builder(scope.toSet()).build()
}
