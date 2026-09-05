package com.teacher.service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teacher.common.constant.ErrorCode;
import com.teacher.common.constant.IBaseErrorCode;
import com.teacher.common.dto.SearchResponseDTO;
import com.teacher.common.exception.BadRequestException;
import com.teacher.common.service.BaseService;
import com.teacher.common.util.DTOMapper;
import com.teacher.dto.role.CreateRoleRequest;
import com.teacher.dto.role.RoleDTO;
import com.teacher.dto.role.UpdateRoleRequest;
import com.teacher.entity.Role;
import com.teacher.repository.IRoleRepository;
import com.teacher.repository.IUserTenantRoleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService extends BaseService<Role, UUID> {

    private final IRoleRepository roleRepository;
    private final IUserTenantRoleRepository userTenantRoleRepository;
    private final DTOMapper dtoMapper;
    private final ObjectMapper objectMapper;

    @Override
    public Optional<Role> findById(UUID id) {
        return roleRepository.findById(id);
    }

    @Override
    public List<Role> findAllById(Collection<UUID> ids) {
        return roleRepository.findAllById(ids);
    }

    @Override
    public String notFoundByIdErrorCode() {
        return IBaseErrorCode.ERROR_NOT_FOUND_ROLE_BY_ID;
    }

    @Override
    public String notFoundByIdsErrorCode() {
        return IBaseErrorCode.ERROR_NOT_FOUND_SOME_ROLES_BY_ID;
    }

    /**
     * Retrieve paginated roles list with optional keyword search.
     */
    public SearchResponseDTO<List<RoleDTO>> getRoles(Integer page, Integer limit, String search) {
        int pageIndex = (page != null && page >= 0) ? page : 0;
        int pageSize = (limit != null && limit > 0) ? limit : 10;

        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Role> rolePage;
        if (search != null && !search.trim().isEmpty()) {
            rolePage = roleRepository.searchRoles(search.trim(), pageable);
        } else {
            rolePage = roleRepository.findAll(pageable);
        }

        return dtoMapper.mapSearchResult(rolePage, RoleDTO.class);
    }

    /**
     * Retrieve single role by ID.
     */
    public RoleDTO getRoleById(UUID id) {
        Role role = findByIdOrThrow(id);
        return dtoMapper.map(role, RoleDTO.class);
    }

    /**
     * Create a new role.
     */
    @Transactional
    public RoleDTO createRole(CreateRoleRequest request) {
        if (roleRepository.existsByCode(request.getCode())) {
            throw new BadRequestException(ErrorCode.ERROR_ROLE_CODE_EXISTED);
        }

        String permissionsJson = serializePermissions(request.getPermissions());
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        Role role = Role.builder()
                .code(request.getCode().trim())
                .name(request.getName().trim())
                .hierarchyLevel(request.getHierarchyLevel())
                .permissions(permissionsJson)
                .isSystemRole(Boolean.TRUE.equals(request.getIsSystemRole()))
                .description(request.getDescription())
                .createdAt(now)
                .updateAt(now)
                .build();

        Role saved = roleRepository.save(role);
        return dtoMapper.map(saved, RoleDTO.class);
    }

    /**
     * Update an existing role.
     */
    @Transactional
    public RoleDTO updateRole(UUID id, UpdateRoleRequest request) {
        Role role = findByIdOrThrow(id);

        if (request.getName() != null && !request.getName().isBlank()) {
            role.setName(request.getName().trim());
        }
        if (request.getHierarchyLevel() != null) {
            role.setHierarchyLevel(request.getHierarchyLevel());
        }
        if (request.getPermissions() != null) {
            role.setPermissions(serializePermissions(request.getPermissions()));
        }
        if (request.getDescription() != null) {
            role.setDescription(request.getDescription());
        }

        role.setUpdateAt(OffsetDateTime.now(ZoneOffset.UTC));
        Role updated = roleRepository.save(role);
        return dtoMapper.map(updated, RoleDTO.class);
    }

    /**
     * Delete role by ID.
     */
    @Transactional
    public void deleteRole(UUID id) {
        Role role = findByIdOrThrow(id);

        if (Boolean.TRUE.equals(role.getIsSystemRole())) {
            throw new BadRequestException(ErrorCode.ERROR_CANNOT_DELETE_SYSTEM_ROLE);
        }

        if (userTenantRoleRepository.existsByRoleId(id)) {
            throw new BadRequestException(ErrorCode.ERROR_ROLE_IN_USE);
        }

        roleRepository.delete(role);
    }

    private String serializePermissions(Object permissions) {
        if (permissions == null) {
            return "{}";
        }
        if (permissions instanceof String str) {
            String trimmed = str.trim();
            if (trimmed.isEmpty()) {
                return "{}";
            }
            return trimmed;
        }
        try {
            return objectMapper.writeValueAsString(permissions);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize permissions object to JSON", e);
            throw new BadRequestException(IBaseErrorCode.ERROR_JSON_PROCESSING);
        }
    }
}
