package com.astitva.collabflow.user.controller;

import com.astitva.collabflow.auth.security.CustomUserDetails;
import com.astitva.collabflow.common.response.ApiResponse;
import com.astitva.collabflow.user.dto.UserProfileResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@EnableMethodSecurity
public class UserController {

    @GetMapping("/me")
    public ApiResponse<UserProfileResponse> getCurrentUser(
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        UserProfileResponse response = new UserProfileResponse(
                currentUser.getId(),
                currentUser.getFullName(),
                currentUser.getEmail(),
                currentUser.getUser().getRole()
        );
        return ApiResponse.success("Current user fetched successfully", response);
    }

//    @PreAuthorize("hasRole('ADMIN')")
//    @GetMapping("/admin-test")
//    public ApiResponse<String> adminTest() {
//        return ApiResponse.success("Admin endpoint accessed successfully", "ADMIN_OK");
//    }
}