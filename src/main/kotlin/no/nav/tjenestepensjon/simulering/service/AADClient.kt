package no.nav.tjenestepensjon.simulering.service

import com.microsoft.aad.msal4j.ClientCredentialFactory.createFromSecret
import com.microsoft.aad.msal4j.ClientCredentialParameters
import com.microsoft.aad.msal4j.ConfidentialClientApplication
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class AADClient(
    @Value("\${AZURE_APP_CLIENT_ID}") private val clientId: String,
    @Value("\${AZURE_APP_CLIENT_SECRET}") private val clientSecret: String,
    @Value("\${AZURE_APP_WELL_KNOWN_URL}") private val authority: String
) {
    private val app =
        ConfidentialClientApplication.builder(clientId, createFromSecret(clientSecret)).authority(authority).build()

    fun getToken(vararg scope: String): String =
        app.acquireToken(ClientCredentialParameters.builder(scope.toSet()).build()).get().accessToken()
}
