package com.teacher.service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teacher.dto.admin.AdminLoginRequest;
import com.teacher.dto.admin.AdminRequestDTO;

import com.teacher.common.constant.ErrorCode;
import com.teacher.common.exception.BadRequestException;
import com.teacher.common.service.BaseService;
import com.teacher.common.util.DTOMapper;
import com.teacher.dto.admin.AdminDTO;
import com.teacher.dto.admin.AdminUpdateRequest;
import com.teacher.dto.admin.ChangePasswordRequest;
import com.teacher.dto.admin.LoginResponse;
import com.teacher.entity.Admin;
import com.teacher.repository.IAdminRepository;
import com.teacher.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService extends BaseService<Admin, UUID>{

    private final IAdminRepository adminRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final DTOMapper dtoMapper;

    @Override
    public Optional<Admin> findById(UUID id) {
        return adminRepository.findById(id);
    }

    @Override
    public List<Admin> findAllById(Collection<UUID> ids) {
        return adminRepository.findAllById(ids);
    }

    @Override
    public String notFoundByIdErrorCode() {
        return ErrorCode.ERROR_USER_NOT_FOUND;
    }

    @Override
    public String notFoundByIdsErrorCode() {
        return ErrorCode.ERROR_SOME_USERS_NOT_FOUND;
    }

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
                .admin(dtoMapper.map(admin, AdminDTO.class))
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

    public Admin getProfile(UUID adminId) {
        return findByIdOrThrow(adminId);
    }

    // -------------------------------------------------------------------------
    // Update profile
    // -------------------------------------------------------------------------

    @Transactional
    public Admin updateProfile(UUID adminId, AdminUpdateRequest request) {
        Admin admin = findByIdOrThrow(adminId);

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

        return adminRepository.save(admin);
    }

    // -------------------------------------------------------------------------
    // Change password
    // -------------------------------------------------------------------------

    @Transactional
    public void changePassword(UUID adminId, ChangePasswordRequest request) {
        Admin admin = findByIdOrThrow(adminId);

        if (!passwordEncoder.matches(request.getCurrentPassword(), admin.getPassword())) {
            throw new BadRequestException(ErrorCode.ERROR_PASSWORD_INCORRECT);
        }

        admin.setPassword(passwordEncoder.encode(request.getNewPassword()));
        admin.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        adminRepository.save(admin);
    }

    // -------------------------------------------------------------------------
    // Register Admin
    // -------------------------------------------------------------------------

    @Transactional
    public Admin register(AdminRequestDTO request) {
        if (adminRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException(ErrorCode.ERROR_EMAIL_EXISTED);
        }

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        Admin admin = Admin.builder()
                .email(request.getEmail())
                .surname(request.getSurname())
                .firstName(request.getFirstName())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        admin.setCreatedAt(now);
        admin.setUpdatedAt(now);

        return adminRepository.save(admin);
    }
}
