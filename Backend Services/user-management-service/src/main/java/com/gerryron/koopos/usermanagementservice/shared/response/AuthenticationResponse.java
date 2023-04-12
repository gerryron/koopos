package com.gerryron.koopos.usermanagementservice.shared.response;

public class AuthenticationResponse {

    private String accessToken;

    AuthenticationResponse(Builder builder) {
        this.accessToken = builder.accessToken;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getAccessToken() {
        return accessToken;
    }

    public static class Builder {
        private String accessToken;

        public Builder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public AuthenticationResponse build() {
            return new AuthenticationResponse(this);
        }
    }
}
