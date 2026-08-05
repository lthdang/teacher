package com.teachermanagement.teacher_management.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teachermanagement.teacher_management.common.util.DTOMapper;
import com.teachermanagement.teacher_management.dto.admin.AdminLoginRequest;
import com.teachermanagement.teacher_management.dto.admin.AdminRequestDTO;
import com.teachermanagement.teacher_management.dto.admin.AdminDTO;
import com.teachermanagement.teacher_management.dto.admin.AdminUpdateRequest;
import com.teachermanagement.teacher_management.dto.admin.ChangePasswordRequest;
import com.teachermanagement.teacher_management.dto.admin.LoginResponse;
import com.teachermanagement.teacher_management.entity.Admin;
import com.teachermanagement.teacher_management.service.AdminService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final DTOMapper dtoMapper;
    /**
     * POST /api/admin/login
     * Public endpoint — authenticates the admin and returns a JWT access token.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody AdminLoginRequest request) {
        LoginResponse response = adminService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/admin/logout
     * Protected — stateless logout; the client should discard the token.
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(Authentication authentication) {
        UUID adminId = UUID.fromString(authentication.getName());
        adminService.logout(adminId);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/admin/profile
     * Protected — returns the authenticated admin's profile.
     */
    @GetMapping("/profile")
    public AdminDTO getProfile(Authentication authentication) {
        UUID adminId = UUID.fromString(authentication.getName());
        Admin response = adminService.getProfile(adminId);
        return dtoMapper.map(response, AdminDTO.class);
    }

    /**
     * PUT /api/admin/profile
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
     * POST /api/admin/register
     * Public endpoint — registers a new admin and returns saved admin details.
     */
    @PostMapping("/register")
    public AdminDTO register(@Valid @RequestBody AdminRequestDTO request) {
        Admin response = adminService.register(request);
        return dtoMapper.map(response, AdminDTO.class);
    }

    /**
     * PUT /api/admin/password
     * Protected — changes the admin's password after verifying the current one.
     */
    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request) {
        UUID adminId = UUID.fromString(authentication.getName());
        adminService.changePassword(adminId, request);
        return ResponseEntity.noContent().build();
    }
}
