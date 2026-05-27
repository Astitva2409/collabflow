package com.astitva.collabflow.auth.service;

import com.astitva.collabflow.auth.dto.RegisterRequest;
import com.astitva.collabflow.auth.dto.RegisterResponse;
import com.astitva.collabflow.common.exception.BadRequestException;
import com.astitva.collabflow.user.entity.User;
import com.astitva.collabflow.user.entity.UserRole;
import com.astitva.collabflow.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new BadRequestException("Email is already registered");
        }

        User user = User.builder()
                .fullName(request.fullName().trim())
                .email(normalizedEmail)
                .password(passwordEncoder.encode(request.password()))
                .role(UserRole.USER)
                .build();

        User savedUser = userRepository.save(user);

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                savedUser.getRole()
        );
    }
}