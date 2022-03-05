package com.example.hairstyle.payload.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class RoleAccountRequest {
    @NotBlank(message = "Username is mandatory")
    private String username;

    @NotNull(message = "Role is not null")
    private Long roleId;
}
