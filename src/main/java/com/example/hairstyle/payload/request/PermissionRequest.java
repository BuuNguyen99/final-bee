package com.example.hairstyle.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionRequest {
    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotBlank(message = "Codename is mandatory")
    private String codename;
}
