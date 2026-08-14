package com.teacher.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teacher.common.dto.ResponseDTO;
import com.teacher.common.util.DTOMapper;
import com.teacher.dto.admin.AdminDTO;
import com.teacher.dto.admin.AdminLoginRequest;
import com.teacher.dto.admin.AdminRequestDTO;
import com.teacher.dto.admin.AdminUpdateRequest;
import com.teacher.dto.admin.ChangePasswordRequest;
import com.teacher.dto.admin.LoginResponse;
import com.teacher.dto.permission.PermissionDTO;
import com.teacher.dto.permission.SubAdminDetailDTO;
import com.teacher.dto.permission.UpdateSubAdminPermissionsRequest;
import com.teacher.entity.Admin;
import com.teacher.security.annotation.RequirePermission;
import com.teacher.service.AdminService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping({"/api/admin", "/api/auth"})
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final DTOMapper dtoMapper;

    /**
     * POST /api/auth/login
     * Public endpoint — authenticates the admin and returns a JWT access token along with permissions.
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
    @RequirePermission("permission.update_admin_info")
    public AdminDTO updateProfile(
            Authentication authentication,
            @RequestBody AdminUpdateRequest request) {
        UUID adminId = UUID.fromString(authentication.getName());
        Admin response = adminService.updateProfile(adminId, request);
        return dtoMapper.map(response, AdminDTO.class);
    }

    /**
     * POST /api/auth/register
     * Protected — requires SUPER_ADMIN (or permission.create_sub_admin).
     * Registers a new SUB_ADMIN account.
     */
    @PostMapping("/register")
    @RequirePermission("permission.create_sub_admin")
    public AdminDTO register(@Valid @RequestBody AdminRequestDTO request) {
        Admin response = adminService.register(request);
        return dtoMapper.map(response, AdminDTO.class);
    }

    /**
     * GET /api/auth/sub-admins
     * Protected — requires SUPER_ADMIN (or permission.view_sub_admins).
     * Returns the list of all SUB_ADMIN accounts.
     */
    @GetMapping("/sub-admins")
    @RequirePermission("permission.view_sub_admins")
    public List<AdminDTO> getSubAdmins() {
        List<Admin> subAdmins = adminService.getSubAdmins();
        return dtoMapper.map(subAdmins, AdminDTO.class);
    }

    /**
     * GET /api/auth/sub-admins/{id}
     * Protected — requires SUPER_ADMIN (or permission.view_sub_admins).
     * Returns a sub-admin's details along with their assigned permissions.
     */
    @GetMapping("/sub-admins/{id}")
    @RequirePermission("permission.view_sub_admins")
    public SubAdminDetailDTO getSubAdminDetail(@PathVariable UUID id) {
        return adminService.getSubAdminDetail(id);
    }

    /**
     * DELETE /api/auth/sub-admins/{id}
     * Protected — requires SUPER_ADMIN (or permission.delete_sub_admin).
     * Soft deletes a SUB_ADMIN account and removes their permissions.
     */
    @DeleteMapping("/sub-admins/{id}")
    @RequirePermission("permission.delete_sub_admin")
    public ResponseEntity<ResponseDTO<?>> deleteSubAdmin(@PathVariable UUID id) {
        adminService.softDeleteSubAdmin(id);
        return ResponseEntity.ok(ResponseDTO.success(null, "Sub-admin deleted successfully."));
    }

    /**
     * PUT /api/auth/sub-admins/{id}/permissions
     * Protected — requires SUPER_ADMIN (or permission.manage_admin_permissions).
     * Fully replaces the sub-admin's assigned permission list.
     */
    @PutMapping("/sub-admins/{id}/permissions")
    @RequirePermission("permission.manage_admin_permissions")
    public List<PermissionDTO> replaceSubAdminPermissions(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateSubAdminPermissionsRequest request) {
        return adminService.replaceSubAdminPermissions(id, request.getPermissionIds());
    }

    /**
     * PUT /api/auth/password
     * Protected — changes the admin's password after verifying the current one.
     */
    @PutMapping("/password")
    @RequirePermission("permission.change_password")
    public ResponseEntity<ResponseDTO<?>> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request) {
        UUID adminId = UUID.fromString(authentication.getName());
        adminService.changePassword(adminId, request);
        return ResponseEntity.ok(ResponseDTO.success(null, "Password changed successfully."));
    }
}
