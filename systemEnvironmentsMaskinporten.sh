#!/bin/bash

export client_id=$(cat /var/run/secrets/nais.io/vault/jwks/client_id)
export jwk_public=$(cat /var/run/secrets/nais.io/vault/jwks/jwk_public)
export jwk_private=$(cat /var/run/secrets/nais.io/vault/jwks/jwk_private)
export private_key_base64=$(cat /var/run/secrets/nais.io/vault/jwks/private_key_base64)