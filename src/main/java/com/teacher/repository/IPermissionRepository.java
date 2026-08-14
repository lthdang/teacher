package com.teacher.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.teacher.entity.Permission;

public interface IPermissionRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findByPermissionCode(String permissionCode);

    boolean existsByPermissionCode(String permissionCode);

    @Query("SELECT p FROM Permission p JOIN AdminPermission ap ON p.id = ap.permissionId WHERE ap.adminId = :adminId")
    List<Permission> findPermissionsByAdminId(@Param("adminId") UUID adminId);
}
