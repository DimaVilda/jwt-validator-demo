package com.jwtvalidation.demo.security.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.jwtvalidation.demo.security.httpclient.KeycloakJwksClient;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Repository
public class JwksRepository {
    private static final Logger log = Logger.getLogger(JwksRepository.class.getName());

    private final KeycloakJwksClient keycloakJwksClient;

    public JwksRepository(KeycloakJwksClient keycloakJwksClient) {
        this.keycloakJwksClient = keycloakJwksClient;
    }

    /**
     * Get a map of all keys from the JWKS endpoint
     */
    @Cacheable("jwksCache")
    public Map<String, PublicKey > getKeysMap() {
        log.info("CACHE MISS - Fetching fresh JWKS from Keycloak");
        JsonNode jwks = keycloakJwksClient.getJsonWebKeys();
        Map<String, PublicKey > keysMap = new HashMap<>();

        JsonNode keys = jwks.get("keys");
        for (JsonNode keyNode : keys) {
            try {
                String kid = keyNode.get("kid").asText();
                PublicKey publicKey = createPublicKeyFromCertificate(keyNode.get("n").asText(), keyNode.get("e").asText());
                keysMap.put(kid, publicKey);
                log.info("Added key with ID to cache: " + kid);
            } catch (Exception e) {
                log.warning("Error processing key from JWKS: " + e.getMessage());
            }
        }

        log.info("Retrieved keys from JWKS: " + keysMap.size());
        return keysMap;
    }

    private PublicKey  createPublicKeyFromCertificate(String modulusStr, String  exponentStr) throws Exception {
        BigInteger modulus = new BigInteger(1,Base64.getUrlDecoder().decode(modulusStr));
        BigInteger publicExponent = new BigInteger(1, Base64.getUrlDecoder().decode(exponentStr));
        KeyFactory kf = KeyFactory.getInstance("RSA"); // list of supported algorithms https://docs.oracle.com/javase/6/docs/technotes/guides/security/StandardNames.html#KeyFactory
        return kf.generatePublic(new RSAPublicKeySpec(modulus, publicExponent));
    }
}
