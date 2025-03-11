package com.jwtvalidation.demo.security.httpclient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Logger;

/**
 * Client for retrieving JWKS from Keycloak
 */
@Component
public class KeycloakJwksClient {
    private static final Logger log = Logger.getLogger(KeycloakJwksClient.class.getName());

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${keycloak.jwks-url}")
    private String jwksUrl;

    public KeycloakJwksClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Get JWKS from Keycloak with circuit breaker and bulkhead
     */
    @CircuitBreaker(name = "keycloakJwks", fallbackMethod = "getJwksFallback")
    @Bulkhead(name = "keycloakJwks")
    public JsonNode getJsonWebKeys() {
        log.info("Fetching JWKS from Keycloak URL: " + jwksUrl);

        try {
            String jwksResponse = restTemplate.getForObject(jwksUrl, String.class);
            return objectMapper.readTree(jwksResponse);
        } catch (Exception e) {
            log.warning("Error fetching JWKS from Keycloak: " + e.getMessage());
            throw new RuntimeException("Failed to fetch JWKS", e);
        }
    }

    /**
     * Fallback method when circuit is open
     */
    public JsonNode getJwksFallback(Exception e) {
        log.warning("Using fallback for JWKS retrieval due to: " + e.getMessage());
        throw new RuntimeException("JWKS service unavailable", e);
    }
}
