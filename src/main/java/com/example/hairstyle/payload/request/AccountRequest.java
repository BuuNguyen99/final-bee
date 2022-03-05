package com.example.hairstyle.payload.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@NoArgsConstructor
public class AccountRequest {
    @NotBlank(message = "Username is mandatory")
    private String username;

    @NotBlank(message = "Firstname is mandatory")
    private String firstname;

    @NotBlank(message = "Lastname is mandatory")
    private String lastname;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String confirmedPassword;

    private Boolean gender;

    private Date birthday;

    private String mobile;

    private String address;

    @NotNull
    private Boolean enabled;

}
