package no.nav.tjenestepensjon.simulering.service

import com.microsoft.aad.msal4j.ClientCredentialFactory.createFromSecret
import com.microsoft.aad.msal4j.ClientCredentialParameters
import com.microsoft.aad.msal4j.ConfidentialClientApplication
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.net.Proxy

@Service
class AADClient(
    @Value("\${AZURE_APP_CLIENT_ID}") clientId: String,
    @Value("\${AZURE_APP_CLIENT_SECRET}") clientSecret: String,
    @Value("\${AZURE_APP_WELL_KNOWN_URL}") authority: String,
    proxy: Proxy
) {
    private val log = LoggerFactory.getLogger(javaClass)

    init {
        log.debug("Building AAD client:")
        log.debug("Using authority: $authority")
        log.debug("Using proxy: $proxy")
    }

    private val app =
        ConfidentialClientApplication.builder(clientId, createFromSecret(clientSecret)).authority(authority)
            .proxy(proxy).build()

    fun getToken(vararg scope: String): String {
        log.debug("Fetching AAD token with scopes: $scope")
        return app.acquireToken(withScopes(*scope)).get().accessToken()
    }

    private fun withScopes(vararg scope: String) = ClientCredentialParameters.builder(scope.toSet()).build()
}
