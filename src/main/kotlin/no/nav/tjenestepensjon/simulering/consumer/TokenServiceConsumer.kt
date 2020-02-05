package no.nav.tjenestepensjon.simulering.consumer

import no.nav.tjenestepensjon.simulering.domain.Token

interface TokenServiceConsumer {
    val oidcAccessToken: Token?
    val samlAccessToken: Token?
}