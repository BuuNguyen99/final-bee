package com.example.hairstyle.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileRequest {
    private String firstname;

    private String lastname;

    @Email(message = "Invalid email formatter")
    private String email;

    private Boolean gender;

    private String mobile;

    private Date birthday;

    private String address;
}
