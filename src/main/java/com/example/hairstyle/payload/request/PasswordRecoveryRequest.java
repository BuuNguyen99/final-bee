package com.example.hairstyle.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordRecoveryRequest {
    @NotBlank(message = "New password is mandatory")
    private String newPassword;

    @NotBlank(message = "New password is mandatory")
    private String confirmedPassword;
}
