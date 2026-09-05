package com.teacher.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.teacher.entity.Tenant;

@Repository
public interface ITenantRepository extends JpaRepository<Tenant, UUID>, JpaSpecificationExecutor<Tenant> {

    Optional<Tenant> findBySlug(String slug);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, UUID id);

    List<Tenant> findAllByOrderByNameAsc();

    @Query("SELECT t FROM Tenant t WHERE " +
           "(CAST(:search AS string) IS NULL OR CAST(:search AS string) = '' OR " +
           "LOWER(t.name) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')) OR " +
           "LOWER(t.slug) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))) " +
           "ORDER BY t.name ASC")
    List<Tenant> searchTenants(@Param("search") String search);
}
