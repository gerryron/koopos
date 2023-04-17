package com.gerryron.kooposservice.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SignInRequest {
    @NotNull
    private String username;
    @NotNull
    private String email;
    @NotNull
    private String password;
}
