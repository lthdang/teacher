package com.teacher.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teacher.common.dto.ResponseDTO;
import com.teacher.common.util.DTOMapper;
import com.teacher.dto.admin.AdminLoginRequest;
import com.teacher.dto.admin.AdminRequestDTO;
import com.teacher.dto.admin.AdminDTO;
import com.teacher.dto.admin.AdminUpdateRequest;
import com.teacher.dto.admin.ChangePasswordRequest;
import com.teacher.dto.admin.LoginResponse;
import com.teacher.entity.Admin;
import com.teacher.service.AdminService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final DTOMapper dtoMapper;
    /**
     * POST /api/auth/login
     * Public endpoint — authenticates the admin and returns a JWT access token.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody AdminLoginRequest request) {
        LoginResponse response = adminService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/auth/logout
     * Protected — stateless logout; the client should discard the token.
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(Authentication authentication) {
        UUID adminId = UUID.fromString(authentication.getName());
        adminService.logout(adminId);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/auth/profile
     * Protected — returns the authenticated admin's profile.
     */
    @GetMapping("/profile")
    public AdminDTO getProfile(Authentication authentication) {
        UUID adminId = UUID.fromString(authentication.getName());
        Admin response = adminService.getProfile(adminId);
        return dtoMapper.map(response, AdminDTO.class);
    }

    /**
     * PUT /api/auth/profile
     * Protected — updates surname, first_name, and/or avatar.
     */
    @PutMapping("/profile")
    public AdminDTO updateProfile(
            Authentication authentication,
            @RequestBody AdminUpdateRequest request) {
        UUID adminId = UUID.fromString(authentication.getName());
        Admin response = adminService.updateProfile(adminId, request);
        return dtoMapper.map(response, AdminDTO.class);
    }

    /**
     * POST /api/auth/register
     * Public endpoint — registers a new admin and returns saved admin details.
     */
    @PostMapping("/register")
    public AdminDTO register(@Valid @RequestBody AdminRequestDTO request) {
        Admin response = adminService.register(request);
        return dtoMapper.map(response, AdminDTO.class);
    }

    /**
     * PUT /api/auth/password
     * Protected — changes the admin's password after verifying the current one.
     */
    @PutMapping("/password")
    public ResponseEntity<ResponseDTO<?>> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request) {
        UUID adminId = UUID.fromString(authentication.getName());
        adminService.changePassword(adminId, request);
        return ResponseEntity.ok(ResponseDTO.success(null, "Password changed successfully."));
    }
}
