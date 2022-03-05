package com.example.hairstyle.security.jwt;

import com.example.hairstyle.config.AppProperties;
import com.example.hairstyle.security.service.UserDetailsImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class JwtService {
    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    private final SecretKey secretKey;

    private final AppProperties appProperties;

    @Autowired
    public JwtService(SecretKey secretKey, AppProperties appProperties) {
        this.secretKey = secretKey;
        this.appProperties = appProperties;
    }

    public String generateJwtAccessToken(UserDetailsImpl userDetailsImpl) {

        return Jwts.builder()
                .setSubject(userDetailsImpl.getUsername())
                .claim("authorities", userDetailsImpl.getAuthorities())
                .setIssuedAt(new Date())
                .setExpiration(java.sql.Date.valueOf(LocalDate.now()
                        .plusDays(appProperties.getAuth().getJwtAccessTokenExpirationMs())))
                .signWith(secretKey)
                .compact();
    }

    public String generateJwtRefreshToken(UserDetailsImpl userDetailsImpl) {

        return Jwts.builder()
                .setSubject(userDetailsImpl.getUsername())
                .claim("authorities", userDetailsImpl.getAuthorities())
                .setIssuedAt(new Date())
                .setExpiration(java.sql.Date.valueOf(LocalDate.now()
                        .plusDays(appProperties.getAuth().getJwtRefreshTokenExpirationMs())))
                .signWith(secretKey)
                .compact();
    }

    public String getUsernameFromJwtToken(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public List<Map<String, String>> getAuthoritiesFromJwtToken(String token) {
        return (List<Map<String, String>>) Jwts
                .parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("authorities");
    }

    public boolean validateToken(String token) {
        try {
            Jwts
                    .parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            logger.error("JWT signature does not match locally computed signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}
