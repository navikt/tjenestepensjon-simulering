package no.nav.tjenestepensjon.simulering.service

import no.nav.tjenestepensjon.simulering.domain.Token

interface SamlTokenService {
    val samlAccessToken: Token
}
