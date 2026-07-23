package com.teachermanagement.teacher_management.service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teachermanagement.teacher_management.dto.admin.AdminLoginRequest;
import com.teachermanagement.teacher_management.dto.admin.AdminResponseDTO;
import com.teachermanagement.teacher_management.dto.admin.AdminUpdateRequest;
import com.teachermanagement.teacher_management.dto.admin.ChangePasswordRequest;
import com.teachermanagement.teacher_management.dto.admin.LoginResponse;
import com.teachermanagement.teacher_management.entity.Admin;
import com.teachermanagement.teacher_management.repository.IAdminRepository;
import com.teachermanagement.teacher_management.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final IAdminRepository adminRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;

    // -------------------------------------------------------------------------
    // Login
    // -------------------------------------------------------------------------

    @Transactional
    public LoginResponse login(AdminLoginRequest request) {
        Admin admin = adminRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        admin.setLastLogin(OffsetDateTime.now(ZoneOffset.UTC));
        adminRepository.save(admin);

        String token = jwtService.generateToken(admin.getId(), admin.getEmail());
        OffsetDateTime expiresAt = OffsetDateTime.ofInstant(
                Instant.now().plusMillis(jwtService.getExpirationMs()), ZoneOffset.UTC);

        return LoginResponse.builder()
                .token(token)
                .expiresAt(expiresAt)
                .admin(toResponseDTO(admin))
                .build();
    }

    // -------------------------------------------------------------------------
    // Logout  (stateless — client discards token)
    // -------------------------------------------------------------------------

    public void logout(UUID adminId) {
        if (!adminRepository.existsById(adminId)) {
            throw new IllegalStateException("Admin not found");
        }
        // No server-side action required for stateless JWT logout.
    }

    // -------------------------------------------------------------------------
    // Get profile
    // -------------------------------------------------------------------------

    public AdminResponseDTO getProfile(UUID adminId) {
        Admin admin = findAdminById(adminId);
        return toResponseDTO(admin);
    }

    // -------------------------------------------------------------------------
    // Update profile
    // -------------------------------------------------------------------------

    @Transactional
    public AdminResponseDTO updateProfile(UUID adminId, AdminUpdateRequest request) {
        Admin admin = findAdminById(adminId);

        if (request.getSurname() != null) {
            admin.setSurname(request.getSurname());
        }
        if (request.getFirstName() != null) {
            admin.setFirstName(request.getFirstName());
        }
        if (request.getAvatar() != null) {
            admin.setAvatar(request.getAvatar());
        }
        admin.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));

        return toResponseDTO(adminRepository.save(admin));
    }

    // -------------------------------------------------------------------------
    // Change password
    // -------------------------------------------------------------------------

    @Transactional
    public void changePassword(UUID adminId, ChangePasswordRequest request) {
        Admin admin = findAdminById(adminId);

        if (!passwordEncoder.matches(request.getCurrentPassword(), admin.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        admin.setPassword(passwordEncoder.encode(request.getNewPassword()));
        admin.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        adminRepository.save(admin);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private Admin findAdminById(UUID adminId) {
        return adminRepository.findById(adminId)
                .orElseThrow(() -> new IllegalStateException("Admin not found"));
    }

    private AdminResponseDTO toResponseDTO(Admin admin) {
        return AdminResponseDTO.builder()
                .id(admin.getId())
                .email(admin.getEmail())
                .surname(admin.getSurname())
                .firstName(admin.getFirstName())
                .avatar(admin.getAvatar())
                .lastLogin(admin.getLastLogin())
                .createdAt(admin.getCreatedAt())
                .updatedAt(admin.getUpdatedAt())
                .build();
    }
}
