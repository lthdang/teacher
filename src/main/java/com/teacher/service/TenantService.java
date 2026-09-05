package com.teacher.service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teacher.common.constant.ErrorCode;
import com.teacher.common.constant.IBaseErrorCode;
import com.teacher.common.exception.BadRequestException;
import com.teacher.common.service.BaseService;
import com.teacher.common.util.DTOMapper;
import com.teacher.dto.tenant.CreateTenantRequest;
import com.teacher.dto.tenant.TenantDTO;
import com.teacher.dto.tenant.TenantSimpleDTO;
import com.teacher.dto.tenant.UpdateTenantRequest;
import com.teacher.entity.Tenant;
import com.teacher.repository.ITenantRepository;
import com.teacher.repository.IUserTenantRoleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TenantService extends BaseService<Tenant, UUID> {

    private final ITenantRepository tenantRepository;
    private final IUserTenantRoleRepository userTenantRoleRepository;
    private final DTOMapper dtoMapper;
    private final ObjectMapper objectMapper;

    @Override
    public Optional<Tenant> findById(UUID id) {
        return tenantRepository.findById(id);
    }

    @Override
    public List<Tenant> findAllById(Collection<UUID> ids) {
        return tenantRepository.findAllById(ids);
    }

    @Override
    public String notFoundByIdErrorCode() {
        return ErrorCode.ERROR_NOT_FOUND_TENANT_BY_ID;
    }

    @Override
    public String notFoundByIdsErrorCode() {
        return ErrorCode.ERROR_NOT_FOUND_SOME_TENANTS_BY_ID;
    }

    /**
     * Retrieve list of tenants with optional search query.
     */
    public List<TenantSimpleDTO> getTenants(String search) {
        List<Tenant> tenants;
        if (search != null && !search.trim().isEmpty()) {
            tenants = tenantRepository.searchTenants(search.trim());
        } else {
            tenants = tenantRepository.findAllByOrderByNameAsc();
        }
        return dtoMapper.map(tenants, TenantSimpleDTO.class);
    }

    /**
     * Retrieve tenant details by ID.
     */
    public TenantDTO getTenantById(UUID id) {
        Tenant tenant = findByIdOrThrow(id);
        return dtoMapper.map(tenant, TenantDTO.class);
    }

    /**
     * Create a new tenant.
     */
    @Transactional
    public TenantDTO createTenant(CreateTenantRequest request) {
        String slug = request.getSlug().trim();
        if (tenantRepository.existsBySlug(slug)) {
            throw new BadRequestException(ErrorCode.ERROR_TENANT_SLUG_EXISTED);
        }

        String settingsJson = serializeSettings(request.getSettings());
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        Tenant tenant = Tenant.builder()
                .name(request.getName().trim())
                .slug(slug)
                .schoolLevel(request.getSchoolLevel())
                .provinceCode(request.getProvinceCode())
                .settings(settingsJson)
                .isActive(request.getIsActive() != null ? request.getIsActive() : Boolean.TRUE)
                .createdAt(now)
                .updatedAt(now)
                .build();

        Tenant saved = tenantRepository.save(tenant);
        return dtoMapper.map(saved, TenantDTO.class);
    }

    /**
     * Update an existing tenant.
     */
    @Transactional
    public TenantDTO updateTenant(UUID id, UpdateTenantRequest request) {
        Tenant tenant = findByIdOrThrow(id);

        if (request.getSlug() != null && !request.getSlug().isBlank()) {
            String newSlug = request.getSlug().trim();
            if (tenantRepository.existsBySlugAndIdNot(newSlug, id)) {
                throw new BadRequestException(ErrorCode.ERROR_TENANT_SLUG_EXISTED);
            }
            tenant.setSlug(newSlug);
        }

        if (request.getName() != null && !request.getName().isBlank()) {
            tenant.setName(request.getName().trim());
        }
        if (request.getSchoolLevel() != null) {
            tenant.setSchoolLevel(request.getSchoolLevel());
        }
        if (request.getProvinceCode() != null) {
            tenant.setProvinceCode(request.getProvinceCode());
        }
        if (request.getSettings() != null) {
            tenant.setSettings(serializeSettings(request.getSettings()));
        }
        if (request.getIsActive() != null) {
            tenant.setIsActive(request.getIsActive());
        }

        tenant.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        Tenant updated = tenantRepository.save(tenant);
        return dtoMapper.map(updated, TenantDTO.class);
    }

    /**
     * Delete tenant by ID.
     */
    @Transactional
    public void deleteTenant(UUID id) {
        Tenant tenant = findByIdOrThrow(id);

        if (userTenantRoleRepository.existsByTenantId(id)) {
            throw new BadRequestException(ErrorCode.ERROR_TENANT_IN_USE);
        }

        tenantRepository.delete(tenant);
    }

    private String serializeSettings(Object settings) {
        if (settings == null) {
            return "{}";
        }
        if (settings instanceof String str) {
            String trimmed = str.trim();
            if (trimmed.isEmpty()) {
                return "{}";
            }
            return trimmed;
        }
        try {
            return objectMapper.writeValueAsString(settings);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize settings object to JSON", e);
            throw new BadRequestException(IBaseErrorCode.ERROR_JSON_PROCESSING);
        }
    }
}
