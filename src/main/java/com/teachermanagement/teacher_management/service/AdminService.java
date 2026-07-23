package com.teachermanagement.teacher_management.service;

import org.springframework.stereotype.Service;

import com.teachermanagement.teacher_management.dto.response.UserResponse;
import com.teachermanagement.teacher_management.entity.User;
import com.teachermanagement.teacher_management.repository.IUserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

    private static final String SYSTEM_ADMIN_EMAIL = "lthdang@ninepoints.vn";

    private final IUserRepository userRepository;

    public UserResponse getSystemAdminUser() {
        User user = userRepository.findByEmail(SYSTEM_ADMIN_EMAIL)
                .orElseThrow(() -> new IllegalStateException(
                        "System admin user with email '" + SYSTEM_ADMIN_EMAIL + "' not found. " +
                        "Please verify that the V0.0.10 migration has been applied successfully."));

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .status(user.getStatus())
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .updateAt(user.getUpdateAt())
                .build();
    }
}
