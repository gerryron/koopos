package com.gerryron.kooposservice.dto.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class SignUpRequest {
    @NotNull
    @Size(min = 4, max = 25)
    private String username;
    @NotNull
    @Size(min = 4, max = 45)
    private String firstName;
    @NotNull
    @Size(max = 45)
    private String lastName;
    @NotNull
    @Size(min = 8, max = 30)
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")
    private String password;
    @NotNull
    private Integer role;
    @NotNull
    @Email
    @Size(max = 100)
    private String email;
    @NotNull
    @Size(max = 20)
    private String phoneNumber;
    @NotNull
    @Size(max = 100)
    private String address;
}
