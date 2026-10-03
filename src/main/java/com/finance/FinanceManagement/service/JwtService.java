package com.finance.FinanceManagement.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long accessTokenExpirationTime = 1000 * 60 * 60;
    private final long refreshTokenExpirationTime = 1000L * 60 * 60 * 24 * 7;
    public String generateToken(UserDetails userDetails) {

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + accessTokenExpirationTime)
                )
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return Jwts.builder() .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration( new Date( System.currentTimeMillis() + refreshTokenExpirationTime ) )
                .signWith(key) .compact();
    }

    public String extractUsername(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername());
    }
}



