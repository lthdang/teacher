package com.teacher.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.teacher.entity.AdminPermission;
import com.teacher.entity.AdminPermissionId;

public interface IAdminPermissionRepository extends JpaRepository<AdminPermission, AdminPermissionId> {

    List<AdminPermission> findByAdminId(UUID adminId);

    void deleteByAdminId(UUID adminId);

    void deleteByPermissionId(Long permissionId);

    long countByPermissionId(Long permissionId);

    @Query("SELECT COUNT(ap) > 0 FROM AdminPermission ap JOIN Permission p ON ap.permissionId = p.id WHERE ap.adminId = :adminId AND p.permissionCode = :permissionCode")
    boolean existsByAdminIdAndPermissionCode(@Param("adminId") UUID adminId, @Param("permissionCode") String permissionCode);
}
