package com.teacher.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.teacher.dto.permission.CreatePermissionItemRequest;
import com.teacher.dto.permission.DeletePermissionsRequest;
import com.teacher.dto.permission.DeletePermissionsResponse;
import com.teacher.dto.permission.PermissionDTO;
import com.teacher.dto.permission.UpdatePermissionRequest;
import com.teacher.security.annotation.RequirePermission;
import com.teacher.service.PermissionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    /**
     * GET /api/permissions
     * Protected — requires SUPER_ADMIN (or permission.view_permissions).
     * Returns full list of permissions in the system.
     */
    @GetMapping
    @RequirePermission("permission.view_permissions")
    public List<PermissionDTO> getAllPermissions(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        return permissionService.getAllPermissions(page, size);
    }

    /**
     * GET /api/permissions/{id}
     * Protected — requires SUPER_ADMIN (or permission.view_permissions).
     * Returns single permission details by ID.
     */
    @GetMapping("/{id}")
    @RequirePermission("permission.view_permissions")
    public PermissionDTO getPermissionById(@PathVariable Long id) {
        return permissionService.getPermissionById(id);
    }

    /**
     * POST /api/permissions
     * Protected — requires SUPER_ADMIN (or permission.create_permission).
     * Creates one or multiple permissions. Validates for duplicate permission_codes.
     */
    @PostMapping
    @RequirePermission("permission.create_permission")
    public List<PermissionDTO> createPermissions(
            @Valid @RequestBody List<CreatePermissionItemRequest> requests) {
        return permissionService.createPermissions(requests);
    }

    /**
     * PUT /api/permissions/{id}
     * Protected — requires SUPER_ADMIN (or permission.update_permission).
     * Updates an existing permission details.
     */
    @PutMapping("/{id}")
    @RequirePermission("permission.update_permission")
    public PermissionDTO updatePermission(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePermissionRequest request) {
        return permissionService.updatePermission(id, request);
    }

    /**
     * DELETE /api/permissions
     * Protected — requires SUPER_ADMIN (or permission.delete_permission).
     * Deletes one or multiple permissions by ID list, cascade-deleting admin_permission links.
     */
    @DeleteMapping
    @RequirePermission("permission.delete_permission")
    public ResponseEntity<DeletePermissionsResponse> deletePermissions(
            @Valid @RequestBody DeletePermissionsRequest request) {
        DeletePermissionsResponse response = permissionService.deletePermissions(request);
        return ResponseEntity.ok(response);
    }
}
