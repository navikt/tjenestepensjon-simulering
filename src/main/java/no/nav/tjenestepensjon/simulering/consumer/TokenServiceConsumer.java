package no.nav.tjenestepensjon.simulering.consumer;

import no.nav.tjenestepensjon.simulering.domain.Token;

public interface TokenServiceConsumer {

    Token getServiceUserOidcToken();
    Token getServiceUserSamlToken();
}
