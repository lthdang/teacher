package com.teacher.dto.tenant;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.teacher.entity.Tenant.SchoolLevel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateTenantRequest {

    @NotBlank(message = "Tenant name is required")
    @Size(max = 255, message = "Tenant name cannot exceed 255 characters")
    private String name;

    @NotBlank(message = "Tenant slug is required")
    @Size(max = 100, message = "Tenant slug cannot exceed 100 characters")
    private String slug;

    @NotNull(message = "School level is required")
    @JsonProperty("school_level")
    @JsonAlias("schoolLevel")
    private SchoolLevel schoolLevel;

    @Size(max = 2, message = "Province code cannot exceed 2 characters")
    @JsonProperty("province_code")
    @JsonAlias("provinceCode")
    private String provinceCode;

    private Object settings;

    @JsonProperty("is_active")
    @JsonAlias("isActive")
    @Builder.Default
    private Boolean isActive = Boolean.TRUE;
}
