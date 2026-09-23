package com.contatodo.infrastructure.security;

import com.contatodo.application.port.TokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

/**
 * Service for JWT token generation and validation.
 *
 * <p>Infrastructure adapter implementing the application {@link TokenProvider}
 * port.</p>
 */
@Service
public class JwtService implements TokenProvider {

    private static final String BEARER_PREFIX = "Bearer ";

    private final SecretKey secretKey;
    private final long expiration;

    /**
     * Creates a JWT service.
     *
     * @param secret JWT secret from environment.
     * @param expiration JWT expiration in milliseconds.
     */
    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration}") long expiration
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String generateToken(String subject, Map<String, Object> claims) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .claims(claims)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Extracts the subject from a JWT token.
     *
     * @param token JWT token.
     * @return Token subject.
     */
    public String extractSubject(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Extracts all claims from a JWT token.
     *
     * @param token JWT token.
     * @return All claims in the token.
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Validates a JWT token.
     *
     * @param token JWT token.
     * @return True if token is valid.
     */
    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception exception) {
            return false;
        }
    }

    /**
     * Retrieves a claim value from the JWT token of the current request.
     *
     * <p>Returns {@code null} when there is no current authenticated request
     * or the claim is absent.</p>
     *
     * @param claimName Claim name.
     * @return Claim value or {@code null}.
     */
    public String getClaim(String claimName) {
        return currentTokenClaims()
                .map(claims -> claims.get(claimName, String.class))
                .orElse(null);
    }

    private Optional<Claims> currentTokenClaims() {
        Optional<String> token = currentToken();
        if (token.isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of(extractAllClaims(token.get()));
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    private Optional<String> currentToken() {
        try {
            RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
            if (attributes instanceof ServletRequestAttributes servletAttributes) {
                String header = servletAttributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
                if (header != null && header.startsWith(BEARER_PREFIX)) {
                    return Optional.of(header.substring(BEARER_PREFIX.length()));
                }
            }
        } catch (Exception exception) {
            // No request context (e.g. tests or non-web flows): no current token.
        }
        return Optional.empty();
    }
}