package com.teacher.dto.role;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.teacher.common.interfaces.IModelDTO;
import com.teacher.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.ALWAYS)
public class RoleDTO implements IModelDTO<Role> {
    private UUID id;
    private String code;
    private String name;
    private Integer hierarchyLevel;
    private String permissions;

    @JsonProperty("isSystemRole")
    private Boolean isSystemRole;

    private String description;
    private OffsetDateTime createdAt;
    private OffsetDateTime updateAt;
}
