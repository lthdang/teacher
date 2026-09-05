package com.teacher.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.teacher.entity.UserTenantRole;

@Repository
public interface IUserTenantRoleRepository extends JpaRepository<UserTenantRole, UUID> {

    @Query("SELECT COUNT(utr) FROM UserTenantRole utr WHERE utr.role.id = :roleId")
    long countByRoleId(@Param("roleId") UUID roleId);

    default boolean existsByRoleId(UUID roleId) {
        return countByRoleId(roleId) > 0;
    }

    @Query("SELECT COUNT(utr) FROM UserTenantRole utr WHERE utr.tenant.id = :tenantId")
    long countByTenantId(@Param("tenantId") UUID tenantId);

    default boolean existsByTenantId(UUID tenantId) {
        return countByTenantId(tenantId) > 0;
    }
}
