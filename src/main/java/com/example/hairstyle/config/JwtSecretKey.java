package com.example.hairstyle.config;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

@Configuration
public class JwtSecretKey {
    private final AppProperties appProperties;

    @Autowired
    public JwtSecretKey(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @Bean
    public SecretKey secretKey(){
        return Keys.hmacShaKeyFor(appProperties
                .getAuth()
                .getSecretKey()
                .getBytes());
    }
}
