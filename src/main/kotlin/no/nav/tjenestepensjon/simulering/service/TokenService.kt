package no.nav.tjenestepensjon.simulering.service

import no.nav.tjenestepensjon.simulering.domain.Token

interface TokenService {
    val oidcAccessToken: Token?
    val samlAccessToken: Token?
}
