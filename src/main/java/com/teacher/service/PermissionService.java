package com.teacher.service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teacher.common.constant.ErrorCode;
import com.teacher.common.exception.BadRequestException;
import com.teacher.common.exception.NotFoundException;
import com.teacher.common.service.BaseService;
import com.teacher.common.util.DTOMapper;
import com.teacher.dto.permission.AffectedPermissionSummary;
import com.teacher.dto.permission.CreatePermissionItemRequest;
import com.teacher.dto.permission.DeletePermissionsResponse;
import com.teacher.dto.permission.PermissionDTO;
import com.teacher.dto.permission.UpdatePermissionRequest;
import com.teacher.entity.Permission;
import com.teacher.repository.IAdminPermissionRepository;
import com.teacher.repository.IPermissionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PermissionService extends BaseService<Permission, Long>  {

    private final IPermissionRepository permissionRepository;
    private final IAdminPermissionRepository adminPermissionRepository;
    private final DTOMapper dtoMapper;

    @Override
    public Optional<Permission> findById(Long id) {
        return permissionRepository.findById(id);
    }

    @Override
    public List<Permission> findAllById(Collection<Long> ids) {
        return permissionRepository.findAllById(ids);
    }

    @Override
    public String notFoundByIdErrorCode() {
        return ErrorCode.ERROR_USER_NOT_FOUND;
    }

    @Override
    public String notFoundByIdsErrorCode() {
        return ErrorCode.ERROR_SOME_USERS_NOT_FOUND;
    }

    public List<PermissionDTO> getAllPermissions(Integer page, Integer size) {
        List<Permission> permissions = permissionRepository.findAll();
        return dtoMapper.map(permissions, PermissionDTO.class);
    }

    public PermissionDTO getPermissionById(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Permission not found"));
        return dtoMapper.map(permission, PermissionDTO.class);
    }

    @Transactional
    public List<PermissionDTO> createPermissions(List<CreatePermissionItemRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return List.of();
        }

        Set<String> seenCodes = new HashSet<>();
        for (CreatePermissionItemRequest req : requests) {
            if (!seenCodes.add(req.getPermissionCode())) {
                throw new BadRequestException("Duplicate permission code in request: " + req.getPermissionCode());
            }
            if (permissionRepository.existsByPermissionCode(req.getPermissionCode())) {
                throw new BadRequestException("Permission code already exists in database: " + req.getPermissionCode());
            }
        }

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        List<Permission> permissions = requests.stream()
                .map(req -> Permission.builder()
                        .name(req.getName())
                        .permissionCode(req.getPermissionCode())
                        .endpoint(req.getEndpoint())
                        .createdAt(now)
                        .updatedAt(now)
                        .build())
                .toList();

        List<Permission> saved = permissionRepository.saveAll(permissions);
        return dtoMapper.map(saved, PermissionDTO.class);
    }

    @Transactional
    public DeletePermissionsResponse deletePermissions(List<Long> permissionIds) {
        List<AffectedPermissionSummary> deletedList = new ArrayList<>();
        List<Long> notFoundIds = new ArrayList<>();

        if (permissionIds != null) {
            for (Long id : permissionIds) {
                Optional<Permission> permissionOpt = permissionRepository.findById(id);
                if (permissionOpt.isPresent()) {
                    Permission p = permissionOpt.get();
                    long count = adminPermissionRepository.countByPermissionId(id);
                    adminPermissionRepository.deleteByPermissionId(id);
                    permissionRepository.delete(p);
                    deletedList.add(new AffectedPermissionSummary(id, p.getPermissionCode(), count));
                } else {
                    notFoundIds.add(id);
                }
            }
        }

        return DeletePermissionsResponse.builder()
                .deletedPermissions(deletedList)
                .notFoundIds(notFoundIds)
                .build();
    }

    @Transactional
    public PermissionDTO updatePermission(Long id, UpdatePermissionRequest request) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Permission not found"));
        if (request.getName() != null) {
            permission.setName(request.getName());
        }
        if (request.getEndpoint() != null) {
            permission.setEndpoint(request.getEndpoint());
        }
        permission.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));

        Permission updatedPermission = permissionRepository.save(permission);
        return dtoMapper.map(updatedPermission, PermissionDTO.class);
    }
}
