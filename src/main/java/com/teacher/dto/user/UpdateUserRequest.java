package com.teacher.dto.user;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {

    @Size(max = 255, message = "Full name cannot exceed 255 characters")
    @JsonProperty("full_name")
    @JsonAlias("fullName")
    private String fullName;

    @Size(max = 20, message = "Phone cannot exceed 20 characters")
    private String phone;

    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;
}
