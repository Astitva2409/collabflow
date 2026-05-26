package com.astitva.collabflow.common.controller;

import com.astitva.collabflow.common.exception.BadRequestException;
import com.astitva.collabflow.common.exception.ResourceNotFoundException;
import com.astitva.collabflow.common.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/api/v1/health")
    public ApiResponse<Map<String, String>> healthCheck() {
        return ApiResponse.success(
                "CollabFlow backend is running",
                Map.of(
                        "status", "UP",
                        "service", "collabflow-backend"
                )
        );
    }

    @GetMapping("/api/v1/test/not-found")
    public ApiResponse<Object> testNotFoundException() {
        throw new ResourceNotFoundException("Test resource not found");
    }

    @GetMapping("/api/v1/test/bad-request")
    public ApiResponse<Object> testBadRequestException() {
        throw new BadRequestException("Test bad request exception");
    }
}