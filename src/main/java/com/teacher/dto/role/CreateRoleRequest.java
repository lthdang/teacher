package com.teacher.dto.role;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateRoleRequest {

    @NotBlank(message = "Role code is required")
    @Size(max = 50, message = "Role code cannot exceed 50 characters")
    private String code;

    @NotBlank(message = "Role name is required")
    @Size(max = 255, message = "Role name cannot exceed 255 characters")
    private String name;

    @NotNull(message = "Hierarchy level is required")
    private Integer hierarchyLevel;

    private Object permissions;

    @JsonProperty("isSystemRole")
    private Boolean isSystemRole;

    private String description;
}
