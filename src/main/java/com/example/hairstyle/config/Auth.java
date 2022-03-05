package com.example.hairstyle.config;

import lombok.Data;

@Data
public class Auth {
    private String secretKey;
    private String tokenPrefix;
    private Long jwtAccessTokenExpirationMs;
    private Long jwtRefreshTokenExpirationMs;
}
