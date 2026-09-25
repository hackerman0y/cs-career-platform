package com.careerpath.auth.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final SecretKey signingKey;
    private final long expirationHours;

    public JwtService(@Value("${app.security.jwt-secret}") String secret,
            @Value("${app.security.jwt-expiration-hours}") long expirationHours,
            Environment environment) {
        if (environment.acceptsProfiles(Profiles.of("prod")) && (secret == null || secret.length() < 32)) {
            throw new IllegalStateException("JWT_SECRET must be set to a random value of at least 32 characters when the prod profile is active.");
        }
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT_SECRET must be configured for this environment.");
        }
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationHours = expirationHours;
    }

    public String createToken(String email) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(email)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expirationHours, ChronoUnit.HOURS)))
                .signWith(signingKey)
                .compact();
    }

    public String extractEmail(String token) {
        return Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload().getSubject();
    }
}
