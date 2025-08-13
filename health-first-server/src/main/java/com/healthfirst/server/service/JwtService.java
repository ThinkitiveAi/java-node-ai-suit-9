package com.healthfirst.server.service;

import com.healthfirst.server.config.JwtConfig;
import com.healthfirst.server.entity.Patient;
import com.healthfirst.server.entity.Provider;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class JwtService {

    @Autowired
    private JwtConfig jwtConfig;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes());
    }

    public String generateToken(Provider provider) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("provider_id", provider.getUuid().toString());
        claims.put("email", provider.getEmail());
        claims.put("role", "PROVIDER");
        claims.put("specialization", provider.getSpecialization());
        claims.put("verification_status", provider.getVerificationStatus().name());
        
        return createToken(claims, provider.getEmail());
    }

    public String generateToken(Patient patient) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("patient_id", patient.getUuid().toString());
        claims.put("email", patient.getEmail());
        claims.put("role", "PATIENT");
        claims.put("email_verified", patient.getEmailVerified());
        claims.put("phone_verified", patient.getPhoneVerified());
        
        return createToken(claims, patient.getEmail());
    }

    public String generateToken(String userId, String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", userId);
        claims.put("email", email);
        claims.put("role", role);
        
        return createToken(claims, email);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + (jwtConfig.getExpiration() * 1000));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer(jwtConfig.getIssuer())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public UUID extractProviderId(String token) {
        String providerIdStr = extractClaim(token, claims -> claims.get("provider_id", String.class));
        return UUID.fromString(providerIdStr);
    }

    public UUID extractPatientId(String token) {
        String patientIdStr = extractClaim(token, claims -> claims.get("patient_id", String.class));
        return UUID.fromString(patientIdStr);
    }

    public UUID extractUserId(String token) {
        String userIdStr = extractClaim(token, claims -> claims.get("user_id", String.class));
        return UUID.fromString(userIdStr);
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public String extractSpecialization(String token) {
        return extractClaim(token, claims -> claims.get("specialization", String.class));
    }

    public String extractVerificationStatus(String token) {
        return extractClaim(token, claims -> claims.get("verification_status", String.class));
    }

    public Boolean extractEmailVerified(String token) {
        return extractClaim(token, claims -> claims.get("email_verified", Boolean.class));
    }

    public Boolean extractPhoneVerified(String token) {
        return extractClaim(token, claims -> claims.get("phone_verified", Boolean.class));
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean isTokenExpired(String token) {
        try {
            Date expiration = extractExpiration(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    private <T> T extractClaim(String token, java.util.function.Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public long getExpirationTime() {
        return jwtConfig.getExpiration();
    }
} 