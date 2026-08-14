package com.teacher.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "admin_permission", schema = "teacher")
@IdClass(AdminPermissionId.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminPermission {

    @Id
    @Column(name = "admin_id", nullable = false)
    private UUID adminId;

    @Id
    @Column(name = "permission_id", nullable = false)
    private Long permissionId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
