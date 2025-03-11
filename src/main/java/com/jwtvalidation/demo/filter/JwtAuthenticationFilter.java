package com.jwtvalidation.demo.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwtvalidation.demo.security.service.JwksService;
import io.jsonwebtoken.*;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.PublicKey;

import java.util.Base64;
import java.util.Map;
import java.util.logging.Logger;

public class JwtAuthenticationFilter implements Filter {
    private static final Logger log = Logger.getLogger(JwtAuthenticationFilter.class.getName());

    private final JwksService jwksService;
    private static final String EXPECTED_ISSUER = "http://localhost:8080/realms/jwt-demo";

    public JwtAuthenticationFilter(JwksService jwksService) {
        this.jwksService = jwksService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // extract the JWT token from the Authorization header
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warning("No Bearer token found in the request");
            chain.doFilter(request, response);
            return;
        }

        // extract token without "Bearer " prefix
        String accessToken = authHeader.substring(7);
        String tokenKeyId = getKid(accessToken);
        try {
            // get the public key from the JWKS service
            PublicKey publicKey = jwksService.getPublicKey(tokenKeyId);
            log.info("Retrieved public key for JWT verification");

            // verify the token signature and parse claims
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(accessToken);

            // extract and validate claims
            Claims claims = claimsJws.getBody();
            String issuer = claims.getIssuer();

            if (!EXPECTED_ISSUER.equals(issuer)) {
                log.warning("Invalid issuer: " + issuer);
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

        } catch (ExpiredJwtException e) {
            log.warning("JWT token expired: " + e.getMessage());
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        } catch (MalformedJwtException e) {
            log.warning("Malformed JWT token: " + e.getMessage());
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        } catch (UnsupportedJwtException e) {
            log.warning("Unsupported JWT token: " + e.getMessage());
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        } catch (Exception e) {
            log.warning("Error processing JWT token: " + e.getClass().getName() + ", Reason: " + e.getMessage());
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        chain.doFilter(request, response);
    }

    private String getKid(String accessToken) {
        String tokenHeader = accessToken.split("\\.")[0];
        tokenHeader = new String(Base64.getDecoder().decode(tokenHeader.getBytes()));
        log.info("Token Header JSon : "+ tokenHeader);
        try {
            return new ObjectMapper().readValue(tokenHeader, Map.class).get("kid").toString();
        } catch (JsonProcessingException e) {
            log.warning("Error parsing JWT token header for kid claim: " + tokenHeader + ", Reason: " + e.getMessage());
            throw new MalformedJwtException(e.getMessage());
        }
    }
}
