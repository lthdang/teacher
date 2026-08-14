package com.teacher.dto.permission;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateSubAdminPermissionsRequest {

    @NotNull(message = "ERROR_PERMISSION_IDS_REQUIRED")
    private List<Long> permissionIds;
}
