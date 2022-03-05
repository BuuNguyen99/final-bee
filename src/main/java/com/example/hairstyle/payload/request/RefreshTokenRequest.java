package com.example.hairstyle.payload.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class RefreshTokenRequest {
    @NotBlank(message = "Refresh token is mandatory")
    private String refreshToken;
}
