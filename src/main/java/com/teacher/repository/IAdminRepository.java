package com.teacher.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.teacher.entity.Admin;
import com.teacher.entity.AdminType;

public interface IAdminRepository extends JpaRepository<Admin, UUID> {

    Optional<Admin> findByEmailAndIsDeletedFalse(String email);

    boolean existsByEmailAndIsDeletedFalse(String email);

    List<Admin> findByTypeAndIsDeletedFalse(AdminType type);

    Optional<Admin> findByIdAndIsDeletedFalse(UUID id);
}
