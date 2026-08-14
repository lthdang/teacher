package com.teacher.dto.permission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AffectedPermissionSummary {
    private Long id;
    private String permissionCode;
    private long affectedAdminsCount;
}
