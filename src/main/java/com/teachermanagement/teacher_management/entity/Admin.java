package com.teachermanagement.teacher_management.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.teachermanagement.teacher_management.common.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "admin", schema = "teach_management")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Admin extends BaseEntity<UUID> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Email
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "surname", length = 100)
    private String surname;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "avatar", length = 500)
    private String avatar;

    @Column(name = "last_login")
    private OffsetDateTime lastLogin;

}
