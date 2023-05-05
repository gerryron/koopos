package com.gerryron.kooposservice.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class SignInRequest {

    @NotNull
    private String username;

    @NotNull
    private String email;

    @NotNull
    private String password;
}
