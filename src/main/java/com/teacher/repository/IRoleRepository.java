package com.teacher.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.teacher.entity.Role;

@Repository
public interface IRoleRepository extends JpaRepository<Role, UUID>, JpaSpecificationExecutor<Role> {

    Optional<Role> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, UUID id);

    @Query("SELECT r FROM Role r WHERE " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(r.code) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(r.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Role> searchRoles(@Param("search") String search, Pageable pageable);
}
