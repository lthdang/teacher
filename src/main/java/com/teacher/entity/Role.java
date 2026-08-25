package com.teacher.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.teacher.common.interfaces.IModel;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles", schema = "teacher")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role implements IModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "hierarchy_level", nullable = false)
    private Integer hierarchyLevel;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "permissions", columnDefinition = "jsonb", nullable = false)
    private String permissions;

    @Column(name = "is_system_role", nullable = false)
    private Boolean isSystemRole;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "update_at", nullable = false)
    private OffsetDateTime updateAt;
}
