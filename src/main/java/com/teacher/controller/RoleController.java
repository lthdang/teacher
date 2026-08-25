package com.teacher.controller;

import java.util.List;
import java.util.UUID;

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

import com.teacher.common.dto.ResponseDTO;
import com.teacher.common.dto.SearchResponseDTO;
import com.teacher.dto.role.CreateRoleRequest;
import com.teacher.dto.role.RoleDTO;
import com.teacher.dto.role.UpdateRoleRequest;
import com.teacher.security.annotation.RequirePermission;
import com.teacher.service.RoleService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    /**
     * GET /api/roles
     * Protected — requires SUPER_ADMIN (or permission.view_roles).
     * Retrieves paginated roles list with optional keyword search.
     */
    @GetMapping
    @RequirePermission("permission.view_roles")
    public SearchResponseDTO<List<RoleDTO>> getRoles(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String search) {
        return roleService.getRoles(page, limit, search);
    }

    /**
     * GET /api/roles/{id}
     * Protected — requires SUPER_ADMIN (or permission.view_roles).
     * Retrieves role details by ID.
     */
    @GetMapping("/{id}")
    @RequirePermission("permission.view_roles")
    public RoleDTO getRoleById(@PathVariable UUID id) {
        return roleService.getRoleById(id);
    }

    /**
     * POST /api/roles
     * Protected — requires SUPER_ADMIN (or permission.create_role).
     * Creates a new role.
     */
    @PostMapping
    @RequirePermission("permission.create_role")
    public RoleDTO createRole(@Valid @RequestBody CreateRoleRequest request) {
        return roleService.createRole(request);
    }

    /**
     * PUT /api/roles/{id}
     * Protected — requires SUPER_ADMIN (or permission.update_role).
     * Updates an existing role.
     */
    @PutMapping("/{id}")
    @RequirePermission("permission.update_role")
    public RoleDTO updateRole(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateRoleRequest request) {
        return roleService.updateRole(id, request);
    }

    /**
     * DELETE /api/roles/{id}
     * Protected — requires SUPER_ADMIN (or permission.delete_role).
     * Deletes a role by ID.
     */
    @DeleteMapping("/{id}")
    @RequirePermission("permission.delete_role")
    public ResponseEntity<ResponseDTO<?>> deleteRole(@PathVariable UUID id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok(ResponseDTO.success(null, "Role deleted successfully."));
    }
}
