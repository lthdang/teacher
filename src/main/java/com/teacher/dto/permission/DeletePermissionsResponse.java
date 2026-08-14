package com.teacher.dto.permission;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.ALWAYS)
public class DeletePermissionsResponse {
    private List<AffectedPermissionSummary> deletedPermissions;
    private List<Long> notFoundIds;
}
