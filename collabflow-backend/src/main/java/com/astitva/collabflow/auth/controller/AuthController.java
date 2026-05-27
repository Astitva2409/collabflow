package com.astitva.collabflow.auth.controller;

import com.astitva.collabflow.auth.dto.RegisterRequest;
import com.astitva.collabflow.auth.dto.RegisterResponse;
import com.astitva.collabflow.auth.service.AuthService;
import com.astitva.collabflow.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);

        return ApiResponse.success("User registered successfully", response);
    }
}