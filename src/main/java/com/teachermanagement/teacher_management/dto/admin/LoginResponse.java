package com.teachermanagement.teacher_management.dto.admin;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private OffsetDateTime expiresAt;
    private AdminDTO admin;
}
