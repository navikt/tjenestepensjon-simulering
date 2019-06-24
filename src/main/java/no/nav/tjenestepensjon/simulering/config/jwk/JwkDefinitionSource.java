/*
 * Copyright 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Modifications copyright 2019 NAV.
 */
package no.nav.tjenestepensjon.simulering.config.jwk;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.security.jwt.codec.Codecs;
import org.springframework.security.jwt.crypto.sign.EllipticCurveVerifier;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.jwt.crypto.sign.SignatureVerifier;

import no.nav.tjenestepensjon.simulering.config.TokenProviderConfig.TokenProvider;

/**
 * A source for JSON Web Key(s) (JWK) that is solely responsible for fetching (and caching)
 * the JWK Set (a set of JWKs) from the URL of the list of {@link TokenProvider} supplied to the constructor.
 *
 * @author Joe Grandja
 * @author Michael Duergner
 * @see JwkSetConverter
 * @see JwkDefinition
 * @see SignatureVerifier
 * @see <a target="_blank" href="https://tools.ietf.org/html/rfc7517#page-10">JWK Set Format</a>
 */
public class JwkDefinitionSource {
    private final List<TokenProvider> tokenProviders;
    private final Map<String, JwkDefinitionHolder> jwkDefinitions = new ConcurrentHashMap<String, JwkDefinitionHolder>();
    private static final JwkSetConverter jwkSetConverter = new JwkSetConverter();

    public JwkDefinitionSource(List<TokenProvider> tokenProviders) {
        this.tokenProviders = tokenProviders;
    }

    /**
     * Returns the JWK definition matching the provided keyId (&quot;kid&quot;).
     * If the JWK definition is not available in the internal cache then {@link #loadJwkDefinitions(TokenProvider)}
     * will be called (to re-load the cache) and then followed-up with a second attempt to locate the JWK definition.
     *
     * @param keyId the Key ID (&quot;kid&quot;)
     * @return the matching {@link JwkDefinition} or null if not found
     */
    JwkDefinitionHolder getDefinitionLoadIfNecessary(String keyId) {
        JwkDefinitionHolder result = this.getDefinition(keyId);
        if (result != null) {
            return result;
        }
        synchronized (this.jwkDefinitions) {
            result = this.getDefinition(keyId);
            if (result != null) {
                return result;
            }
            this.jwkDefinitions.clear();

            for (TokenProvider tokenProvider : tokenProviders) {
                this.jwkDefinitions.putAll(loadJwkDefinitions(tokenProvider));
            }
            return this.getDefinition(keyId);
        }
    }

    /**
     * Returns the JWK definition matching the provided keyId (&quot;kid&quot;).
     *
     * @param keyId the Key ID (&quot;kid&quot;)
     * @return the matching {@link JwkDefinition} or null if not found
     */
    private JwkDefinitionHolder getDefinition(String keyId) {
        return this.jwkDefinitions.get(keyId);
    }

    /**
     * Fetches the JWK Set from the provided {@link TokenProvider#getJwksUrl()} and
     * returns a <code>Map</code> keyed by the JWK keyId (&quot;kid&quot;)
     * and mapped to an association of the {@link JwkDefinition} and {@link SignatureVerifier}.
     * Requests are proxied through {@link TokenProvider#getProxyUrl()} if present.
     * Uses a {@link JwkSetConverter} to convert the JWK Set URL source to a set of {@link JwkDefinition}(s)
     * followed by the instantiation of a {@link SignatureVerifier} which is associated to it's {@link JwkDefinition}.
     *
     * @param tokenProvider the JWK Set URL
     * @return a <code>Map</code> keyed by the JWK keyId and mapped to an association of {@link JwkDefinition} and {@link SignatureVerifier}
     * @see JwkSetConverter
     */
    static Map<String, JwkDefinitionHolder> loadJwkDefinitions(TokenProvider tokenProvider) {
        InputStream jwkSetSource;
        try {
            URL url = new URL(tokenProvider.getJwksUrl());
            if (tokenProvider.getProxyUrl() != null) {
                String[] proxyUrl = tokenProvider.getProxyUrl().split(":");
                jwkSetSource = url.openConnection(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyUrl[0], Integer.valueOf(proxyUrl[1])))).getInputStream();
            } else {
                jwkSetSource = url.openStream();
            }
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid JWK Set URL: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new JwkException("An I/O error occurred while reading from the JWK Set source: " + e.getMessage(), e);
        }

        Set<JwkDefinition> jwkDefinitionSet = jwkSetConverter.convert(jwkSetSource);

        Map<String, JwkDefinitionHolder> jwkDefinitions = new LinkedHashMap<String, JwkDefinitionHolder>();

        for (JwkDefinition jwkDefinition : jwkDefinitionSet) {
            if (JwkDefinition.KeyType.RSA.equals(jwkDefinition.getKeyType())) {
                jwkDefinitions.put(jwkDefinition.getKeyId(),
                        new JwkDefinitionHolder(jwkDefinition, createRsaVerifier((RsaJwkDefinition) jwkDefinition)));
            } else if (JwkDefinition.KeyType.EC.equals(jwkDefinition.getKeyType())) {
                jwkDefinitions.put(jwkDefinition.getKeyId(),
                        new JwkDefinitionHolder(jwkDefinition, createEcVerifier((EllipticCurveJwkDefinition) jwkDefinition)));
            }
        }

        return jwkDefinitions;
    }

    private static RsaVerifier createRsaVerifier(RsaJwkDefinition rsaDefinition) {
        RsaVerifier result;
        try {
            BigInteger modulus = new BigInteger(1, Codecs.b64UrlDecode(rsaDefinition.getModulus()));
            BigInteger exponent = new BigInteger(1, Codecs.b64UrlDecode(rsaDefinition.getExponent()));

            RSAPublicKey rsaPublicKey = (RSAPublicKey) KeyFactory.getInstance("RSA")
                    .generatePublic(new RSAPublicKeySpec(modulus, exponent));

            if (rsaDefinition.getAlgorithm() != null) {
                result = new RsaVerifier(rsaPublicKey, rsaDefinition.getAlgorithm().standardName());
            } else {
                result = new RsaVerifier(rsaPublicKey);
            }
        } catch (Exception ex) {
            throw new JwkException("An error occurred while creating a RSA Public Key Verifier for " +
                    rsaDefinition.getKeyId() + " : " + ex.getMessage(), ex);
        }
        return result;
    }

    private static EllipticCurveVerifier createEcVerifier(EllipticCurveJwkDefinition ecDefinition) {
        EllipticCurveVerifier result;
        try {
            BigInteger x = new BigInteger(1, Codecs.b64UrlDecode(ecDefinition.getX()));
            BigInteger y = new BigInteger(1, Codecs.b64UrlDecode(ecDefinition.getY()));

            String algorithm = null;
            if (EllipticCurveJwkDefinition.NamedCurve.P256.value().equals(ecDefinition.getCurve())) {
                algorithm = JwkDefinition.CryptoAlgorithm.ES256.standardName();
            } else if (EllipticCurveJwkDefinition.NamedCurve.P384.value().equals(ecDefinition.getCurve())) {
                algorithm = JwkDefinition.CryptoAlgorithm.ES384.standardName();
            } else if (EllipticCurveJwkDefinition.NamedCurve.P521.value().equals(ecDefinition.getCurve())) {
                algorithm = JwkDefinition.CryptoAlgorithm.ES512.standardName();
            }

            result = new EllipticCurveVerifier(x, y, ecDefinition.getCurve(), algorithm);
        } catch (Exception ex) {
            throw new JwkException("An error occurred while creating an EC Public Key Verifier for " +
                    ecDefinition.getKeyId() + " : " + ex.getMessage(), ex);
        }
        return result;
    }

    static class JwkDefinitionHolder {
        private final JwkDefinition jwkDefinition;
        private final SignatureVerifier signatureVerifier;

        private JwkDefinitionHolder(JwkDefinition jwkDefinition, SignatureVerifier signatureVerifier) {
            this.jwkDefinition = jwkDefinition;
            this.signatureVerifier = signatureVerifier;
        }

        JwkDefinition getJwkDefinition() {
            return jwkDefinition;
        }

        SignatureVerifier getSignatureVerifier() {
            return signatureVerifier;
        }
    }
}
