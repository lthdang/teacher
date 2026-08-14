package com.teacher.service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teacher.common.constant.ErrorCode;
import com.teacher.common.exception.BadRequestException;
import com.teacher.common.exception.NotFoundException;
import com.teacher.common.service.BaseService;
import com.teacher.common.util.DTOMapper;
import com.teacher.dto.admin.AdminDTO;
import com.teacher.dto.admin.AdminLoginRequest;
import com.teacher.dto.admin.AdminRequestDTO;
import com.teacher.dto.admin.AdminUpdateRequest;
import com.teacher.dto.admin.ChangePasswordRequest;
import com.teacher.dto.admin.LoginResponse;
import com.teacher.dto.permission.PermissionDTO;
import com.teacher.dto.permission.SubAdminDetailDTO;
import com.teacher.entity.Admin;
import com.teacher.entity.AdminPermission;
import com.teacher.entity.AdminType;
import com.teacher.entity.Permission;
import com.teacher.repository.IAdminPermissionRepository;
import com.teacher.repository.IAdminRepository;
import com.teacher.repository.IPermissionRepository;
import com.teacher.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService extends BaseService<Admin, UUID> {

    private final IAdminRepository adminRepository;
    private final IPermissionRepository permissionRepository;
    private final IAdminPermissionRepository adminPermissionRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final DTOMapper dtoMapper;

    @Override
    public Optional<Admin> findById(UUID id) {
        return adminRepository.findByIdAndIsDeletedFalse(id);
    }

    @Override
    public List<Admin> findAllById(Collection<UUID> ids) {
        return adminRepository.findAllById(ids).stream()
                .filter(admin -> !Boolean.TRUE.equals(admin.getIsDeleted()))
                .toList();
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
        Admin admin = adminRepository.findByEmailAndIsDeletedFalse(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        admin.setLastLogin(OffsetDateTime.now(ZoneOffset.UTC));
        adminRepository.save(admin);

        String token = jwtService.generateToken(admin.getId(), admin.getEmail());
        OffsetDateTime expiresAt = OffsetDateTime.ofInstant(
                Instant.now().plusMillis(jwtService.getExpirationMs()), ZoneOffset.UTC);

        List<String> permissions;
        if (admin.getType() == AdminType.SUPER_ADMIN) {
            permissions = permissionRepository.findAll().stream()
                    .map(Permission::getPermissionCode)
                    .toList();
        } else {
            permissions = permissionRepository.findPermissionsByAdminId(admin.getId()).stream()
                    .map(Permission::getPermissionCode)
                    .toList();
        }

        return LoginResponse.builder()
                .token(token)
                .expiresAt(expiresAt)
                .admin(dtoMapper.map(admin, AdminDTO.class))
                .permissions(permissions)
                .build();
    }

    // -------------------------------------------------------------------------
    // Logout (stateless — client discards token)
    // -------------------------------------------------------------------------

    public void logout(UUID adminId) {
        Admin admin = findById(adminId)
                .orElseThrow(() -> new IllegalStateException("Admin not found"));
        if (Boolean.TRUE.equals(admin.getIsDeleted())) {
            throw new IllegalStateException("Admin not found");
        }
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
    // Register Admin (Creates SUB_ADMIN only)
    // -------------------------------------------------------------------------

    @Transactional
    public Admin register(AdminRequestDTO request) {
        if (adminRepository.existsByEmailAndIsDeletedFalse(request.getEmail())) {
            throw new BadRequestException(ErrorCode.ERROR_EMAIL_EXISTED);
        }

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        Admin admin = Admin.builder()
                .email(request.getEmail())
                .surname(request.getSurname())
                .firstName(request.getFirstName())
                .password(passwordEncoder.encode(request.getPassword()))
                .type(AdminType.SUB_ADMIN)
                .isDeleted(false)
                .build();
        admin.setCreatedAt(now);
        admin.setUpdatedAt(now);

        return adminRepository.save(admin);
    }

    // -------------------------------------------------------------------------
    // Get Sub-Admins List
    // -------------------------------------------------------------------------

    public List<Admin> getSubAdmins() {
        return adminRepository.findByTypeAndIsDeletedFalse(AdminType.SUB_ADMIN);
    }

    // -------------------------------------------------------------------------
    // Get Sub-Admin Detail with Permissions
    // -------------------------------------------------------------------------

    public SubAdminDetailDTO getSubAdminDetail(UUID id) {
        Admin admin = adminRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Sub-admin not found"));

        if (admin.getType() != AdminType.SUB_ADMIN) {
            throw new NotFoundException("Sub-admin not found");
        }

        List<Permission> permissions = permissionRepository.findPermissionsByAdminId(id);
        List<PermissionDTO> permissionDTOs = dtoMapper.map(permissions, PermissionDTO.class);

        return SubAdminDetailDTO.builder()
                .id(admin.getId())
                .email(admin.getEmail())
                .surname(admin.getSurname())
                .firstName(admin.getFirstName())
                .avatar(admin.getAvatar())
                .type(admin.getType())
                .lastLogin(admin.getLastLogin())
                .createdAt(admin.getCreatedAt())
                .updatedAt(admin.getUpdatedAt())
                .permissions(permissionDTOs)
                .build();
    }

    // -------------------------------------------------------------------------
    // Soft Delete Sub-Admin
    // -------------------------------------------------------------------------

    @Transactional
    public void softDeleteSubAdmin(UUID id) {
        Admin admin = adminRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Sub-admin not found"));

        if (admin.getType() == AdminType.SUPER_ADMIN) {
            throw new BadRequestException("Deleting SUPER_ADMIN account is not allowed");
        }

        if (admin.getType() != AdminType.SUB_ADMIN) {
            throw new NotFoundException("Sub-admin not found");
        }

        admin.setIsDeleted(true);
        admin.setDeletedAt(OffsetDateTime.now(ZoneOffset.UTC));
        adminRepository.save(admin);

        // Remove related admin_permission links
        adminPermissionRepository.deleteByAdminId(id);
    }

    // -------------------------------------------------------------------------
    // Update (Full Replace) Permissions for Sub-Admin
    // -------------------------------------------------------------------------

    @Transactional
    public List<PermissionDTO> replaceSubAdminPermissions(UUID id, List<Long> permissionIds) {
        Admin admin = adminRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Sub-admin not found"));

        if (admin.getType() != AdminType.SUB_ADMIN) {
            throw new NotFoundException("Sub-admin not found");
        }

        if (permissionIds != null && !permissionIds.isEmpty()) {
            List<Permission> foundPermissions = permissionRepository.findAllById(permissionIds);
            Set<Long> foundIds = foundPermissions.stream().map(Permission::getId).collect(Collectors.toSet());
            List<Long> missingIds = permissionIds.stream().filter(pid -> !foundIds.contains(pid)).toList();

            if (!missingIds.isEmpty()) {
                throw new BadRequestException("Invalid permission IDs: " + missingIds);
            }
        }

        // Delete existing links
        adminPermissionRepository.deleteByAdminId(id);

        // Insert new links
        if (permissionIds != null && !permissionIds.isEmpty()) {
            OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
            List<AdminPermission> newLinks = permissionIds.stream()
                    .map(pid -> AdminPermission.builder()
                            .adminId(id)
                            .permissionId(pid)
                            .createdAt(now)
                            .build())
                    .toList();
            adminPermissionRepository.saveAll(newLinks);
        }

        List<Permission> updatedPermissions = permissionRepository.findPermissionsByAdminId(id);
        return dtoMapper.map(updatedPermissions, PermissionDTO.class);
    }

    // -------------------------------------------------------------------------
    // Check Permission
    // -------------------------------------------------------------------------

    public boolean hasPermission(UUID adminId, String permissionCode) {
        return adminPermissionRepository.existsByAdminIdAndPermissionCode(adminId, permissionCode);
    }
}
