package dev.sorokin.eventmanager.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenManager {
    private final SecretKey secretKey;
    private final long expirationTime;


    public JwtTokenManager(
            @Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.lifetime}") long expirationTime
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.expirationTime = expirationTime;
    }


    public String generateToken(Authentication authentication) {
        return  Jwts
                .builder()
                .subject(authentication.getName())
                .claim("authorities", authentication.getAuthorities())
                .signWith(secretKey)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .compact();
    }

    public String generateTokenByLogin(String login) {
        return Jwts
                .builder()
                .subject(login)
                .signWith(secretKey)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .compact();
    }

    public String getLoginFromToken(String token) {

        return Jwts
                .parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token) {

        try {
             Jwts.parser()
                     .verifyWith(secretKey)
                     .build()
                     .parseSignedClaims(token);

             return true;
        } catch (ExpiredJwtException e) {
            return false;
        }
    }
}
