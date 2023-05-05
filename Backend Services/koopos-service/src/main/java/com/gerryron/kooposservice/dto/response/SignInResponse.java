package com.gerryron.kooposservice.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignInResponse {

    private String accessToken;
}
