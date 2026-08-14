package com.teacher.service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teacher.common.exception.BadRequestException;
import com.teacher.common.exception.NotFoundException;
import com.teacher.common.util.DTOMapper;
import com.teacher.dto.permission.AffectedPermissionSummary;
import com.teacher.dto.permission.CreatePermissionItemRequest;
import com.teacher.dto.permission.DeletePermissionsRequest;
import com.teacher.dto.permission.DeletePermissionsResponse;
import com.teacher.dto.permission.PermissionDTO;
import com.teacher.dto.permission.UpdatePermissionRequest;
import com.teacher.entity.Permission;
import com.teacher.repository.IAdminPermissionRepository;
import com.teacher.repository.IPermissionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final IPermissionRepository permissionRepository;
    private final IAdminPermissionRepository adminPermissionRepository;
    private final DTOMapper dtoMapper;

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
            throw new BadRequestException("Request list must not be empty");
        }

        Set<String> codeSetInRequest = new HashSet<>();
        List<String> duplicateCodesInRequest = new ArrayList<>();

        for (CreatePermissionItemRequest item : requests) {
            if (!codeSetInRequest.add(item.getPermissionCode())) {
                duplicateCodesInRequest.add(item.getPermissionCode());
            }
        }

        List<String> existingCodesInDb = requests.stream()
                .map(CreatePermissionItemRequest::getPermissionCode)
                .filter(permissionRepository::existsByPermissionCode)
                .toList();

        Set<String> allDuplicates = new HashSet<>(duplicateCodesInRequest);
        allDuplicates.addAll(existingCodesInDb);

        if (!allDuplicates.isEmpty()) {
            throw new BadRequestException("Duplicate permission codes found: " + allDuplicates);
        }

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        List<Permission> newPermissions = requests.stream()
                .map(req -> Permission.builder()
                        .name(req.getName())
                        .permissionCode(req.getPermissionCode())
                        .endpoint(req.getEndpoint())
                        .createdAt(now)
                        .updatedAt(now)
                        .build())
                .toList();

        List<Permission> savedPermissions = permissionRepository.saveAll(newPermissions);
        return dtoMapper.map(savedPermissions, PermissionDTO.class);
    }

    @Transactional
    public DeletePermissionsResponse deletePermissions(DeletePermissionsRequest request) {
        List<Long> requestedIds = request.getPermissionIds();
        List<AffectedPermissionSummary> deletedSummaries = new ArrayList<>();
        List<Long> notFoundIds = new ArrayList<>();

        for (Long id : requestedIds) {
            Optional<Permission> permissionOpt = permissionRepository.findById(id);
            if (permissionOpt.isEmpty()) {
                notFoundIds.add(id);
            } else {
                Permission p = permissionOpt.get();
                long affectedAdmins = adminPermissionRepository.countByPermissionId(id);

                // Cascade delete junction links
                adminPermissionRepository.deleteByPermissionId(id);

                // Delete permission
                permissionRepository.deleteById(id);

                deletedSummaries.add(AffectedPermissionSummary.builder()
                        .id(p.getId())
                        .permissionCode(p.getPermissionCode())
                        .affectedAdminsCount(affectedAdmins)
                        .build());
            }
        }

        return DeletePermissionsResponse.builder()
                .deletedPermissions(deletedSummaries)
                .notFoundIds(notFoundIds)
                .build();
    }

    @Transactional
    public PermissionDTO updatePermission(Long id, UpdatePermissionRequest request) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Permission not found"));

        if (!permission.getPermissionCode().equals(request.getPermissionCode())) {
            if (permissionRepository.existsByPermissionCode(request.getPermissionCode())) {
                throw new BadRequestException("Permission code already exists: " + request.getPermissionCode());
            }
        }

        permission.setName(request.getName());
        permission.setPermissionCode(request.getPermissionCode());
        permission.setEndpoint(request.getEndpoint());
        permission.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));

        Permission updatedPermission = permissionRepository.save(permission);
        return dtoMapper.map(updatedPermission, PermissionDTO.class);
    }
}
