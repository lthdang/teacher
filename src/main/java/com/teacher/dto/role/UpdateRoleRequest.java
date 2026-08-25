package com.teacher.dto.role;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRoleRequest {

    @Size(max = 255, message = "Role name cannot exceed 255 characters")
    private String name;

    private Integer hierarchyLevel;

    private Object permissions;

    private String description;
}
