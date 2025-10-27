package com.faculdade.sgca.infrastructure.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${sgca.security.jwt.secret}")
    private String secretKey;

    @Value("${sgca.security.jwt.expiration-minutes}")
    private long expirationMinutes;

    // ============================================================
    // 🔹 GERAR TOKEN JWT
    // ============================================================
    public String generateToken(String username) {
        Date now = new Date(System.currentTimeMillis());
        Date expiration = new Date(now.getTime() + expirationMinutes * 60 * 1000);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ============================================================
    // 🔹 EXTRAIR USERNAME (E-MAIL)
    // ============================================================
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // ============================================================
    // 🔹 VALIDAR TOKEN
    // ============================================================
    public boolean isTokenValid(String token, String username) {
        try {
            String subject = extractUsername(token);
            return subject.equals(username) && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false; // Token malformado, expirado ou inválido
        }
    }

    // ============================================================
    // 🔹 VERIFICAR EXPIRAÇÃO
    // ============================================================
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // ============================================================
    // 🔹 EXTRAIR CLAIM GENÉRICA
    // ============================================================
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // ============================================================
    // 🔹 DECODIFICAR E VALIDAR ASSINATURA
    // ============================================================
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token expirado.");
        } catch (JwtException e) {
            throw new RuntimeException("Token inválido.");
        }
    }

    // ============================================================
    // 🔹 GERAR CHAVE A PARTIR DO SEGREDO BASE64
    // ============================================================
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
