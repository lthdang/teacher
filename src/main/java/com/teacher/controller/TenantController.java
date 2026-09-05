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
import com.teacher.dto.tenant.CreateTenantRequest;
import com.teacher.dto.tenant.TenantDTO;
import com.teacher.dto.tenant.TenantSimpleDTO;
import com.teacher.dto.tenant.UpdateTenantRequest;
import com.teacher.security.annotation.RequirePermission;
import com.teacher.service.TenantService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping({"/api/tenants", "/tenants"})
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    /**
     * GET /tenants or GET /api/tenants
     * Retrieves list of tenants with basic information.
     */
    @GetMapping
    @RequirePermission("permission.view_tenants")
    public List<TenantSimpleDTO> getTenants(@RequestParam(required = false) String search) {
        return tenantService.getTenants(search);
    }

    /**
     * GET /tenants/{tenantId} or GET /api/tenants/{tenantId}
     * Retrieves detailed information of a tenant.
     */
    @GetMapping("/{tenantId}")
    @RequirePermission("permission.view_tenants")
    public TenantDTO getTenantById(@PathVariable UUID tenantId) {
        return tenantService.getTenantById(tenantId);
    }

    /**
     * POST /tenants or POST /api/tenants
     * Creates a new tenant.
     */
    @PostMapping
    @RequirePermission("permission.create_tenant")
    public TenantDTO createTenant(@Valid @RequestBody CreateTenantRequest request) {
        return tenantService.createTenant(request);
    }

    /**
     * PUT /tenants/{tenantId} or PUT /api/tenants/{tenantId}
     * Updates an existing tenant.
     */
    @PutMapping("/{tenantId}")
    @RequirePermission("permission.update_tenant")
    public TenantDTO updateTenant(
            @PathVariable UUID tenantId,
            @Valid @RequestBody UpdateTenantRequest request) {
        return tenantService.updateTenant(tenantId, request);
    }

    /**
     * DELETE /tenants/{tenantId} or DELETE /api/tenants/{tenantId}
     * Deletes a tenant by ID.
     */
    @DeleteMapping("/{tenantId}")
    @RequirePermission("permission.delete_tenant")
    public ResponseEntity<ResponseDTO<?>> deleteTenant(@PathVariable UUID tenantId) {
        tenantService.deleteTenant(tenantId);
        return ResponseEntity.ok(ResponseDTO.success(null, "Tenant deleted successfully."));
    }
}
