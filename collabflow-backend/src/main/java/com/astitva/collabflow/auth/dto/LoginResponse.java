package com.astitva.collabflow.auth.dto;

public record LoginResponse(
        String accessToken,
        String tokenType,
        AuthUserResponse user
) {
}
