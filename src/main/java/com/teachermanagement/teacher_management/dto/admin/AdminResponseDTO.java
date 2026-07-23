package com.teachermanagement.teacher_management.dto.admin;

import java.time.OffsetDateTime;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.teachermanagement.teacher_management.entity.User.UserStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.ALWAYS)
public class AdminResponseDTO {
    private UUID id;
    private String email;
    private String fullName;
    private String phone;
    private UserStatus status;
    private OffsetDateTime lastLoginAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updateAt;
}
