package com.astitva.collabflow.user.dto;

import com.astitva.collabflow.user.entity.UserRole;
import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String fullName,
        String email,
        UserRole role
) {
}