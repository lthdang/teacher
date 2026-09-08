package com.teacher.dto.user;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.teacher.common.interfaces.IModelDTO;
import com.teacher.entity.User;
import com.teacher.entity.User.UserStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.ALWAYS)
public class UserDTO implements IModelDTO<User> {

    private UUID id;

    private String email;

    @JsonProperty("full_name")
    @JsonAlias("fullName")
    private String fullName;

    private String phone;

    private UserStatus status;

    @JsonProperty("last_login_at")
    @JsonAlias("lastLoginAt")
    private OffsetDateTime lastLoginAt;

    @JsonProperty("created_at")
    @JsonAlias("createdAt")
    private OffsetDateTime createdAt;

    @JsonProperty("update_at")
    @JsonAlias({"updateAt", "updated_at", "updatedAt"})
    private OffsetDateTime updateAt;
}
