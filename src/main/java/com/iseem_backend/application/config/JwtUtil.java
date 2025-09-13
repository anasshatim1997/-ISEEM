package com.iseem_backend.application.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.iseem_backend.application.model.User;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String genererToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId().toString());
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole().name());
        return creerToken(claims, user.getEmail());
    }

    private String creerToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public Boolean validerToken(String token, String email) {
        try {
            final String tokenEmail = extraireEmail(token);
            return (tokenEmail.equals(email) && !estTokenExpire(token));
        } catch (Exception e) {
            return false;
        }
    }

    public String extraireEmail(String token) {
        return extraireClaim(token, Claims::getSubject);
    }

    public String extraireUserId(String token) {
        return (String) extraireTousclaims(token).get("userId");
    }

    public String extraireRole(String token) {
        return (String) extraireTousclaims(token).get("role");
    }

    public Date extraireDateExpiration(String token) {
        return extraireClaim(token, Claims::getExpiration);
    }

    public <T> T extraireClaim(String token, java.util.function.Function<Claims, T> claimsResolver) {
        final Claims claims = extraireTousclaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extraireTousclaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean estTokenExpire(String token) {
        return extraireDateExpiration(token).before(new Date());
    }
}