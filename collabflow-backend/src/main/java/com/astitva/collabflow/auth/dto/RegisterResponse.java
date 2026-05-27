package com.astitva.collabflow.auth.dto;

import com.astitva.collabflow.user.entity.UserRole;

import java.util.UUID;

public record RegisterResponse(
        UUID id,
        String fullName,
        String email,
        UserRole role
) {
}