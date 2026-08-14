package com.teacher.dto.permission;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.teacher.common.interfaces.IModelDTO;
import com.teacher.entity.Permission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.ALWAYS)
public class PermissionDTO implements IModelDTO<Permission> {
    private Long id;
    private String name;
    private String permissionCode;
    private String endpoint;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
