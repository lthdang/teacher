package com.teacher.dto.permission;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatePermissionItemRequest {

    @NotBlank(message = "ERROR_PERMISSION_NAME_REQUIRED")
    private String name;

    @NotBlank(message = "ERROR_PERMISSION_CODE_REQUIRED")
    private String permissionCode;

    @NotBlank(message = "ERROR_PERMISSION_ENDPOINT_REQUIRED")
    private String endpoint;
}
