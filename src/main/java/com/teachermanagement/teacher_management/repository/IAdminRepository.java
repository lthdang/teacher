package com.teachermanagement.teacher_management.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.teachermanagement.teacher_management.entity.Admin;

public interface IAdminRepository extends JpaRepository<Admin, UUID> {

    Optional<Admin> findByEmail(String email);

    boolean existsByEmail(String email);
}
