package com.jwtvalidation.demo.security.service;

import com.jwtvalidation.demo.security.repository.JwksRepository;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.util.Optional;

@Service
public class JwksService {
    private final JwksRepository jwksRepository;

    public JwksService(JwksRepository jwksRepository) {
        this.jwksRepository = jwksRepository;
    }

    /**
     * Get a public key by key ID with caching
     */
    public PublicKey getPublicKey(String keyId) {
        return Optional.ofNullable(
                    jwksRepository.getKeysMap()
                    .get(keyId)
                )
                .orElseThrow(() -> new IllegalArgumentException("Key not found: " + keyId));
    }
}
