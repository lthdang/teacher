package com.teacher.dto.tenant;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.teacher.common.interfaces.IModelDTO;
import com.teacher.entity.Tenant;
import com.teacher.entity.Tenant.SchoolLevel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.ALWAYS)
public class TenantSimpleDTO implements IModelDTO<Tenant> {

    private UUID id;

    private String name;

    private String slug;

    @JsonProperty("school_level")
    @JsonAlias("schoolLevel")
    private SchoolLevel schoolLevel;

    @JsonProperty("province_code")
    @JsonAlias("provinceCode")
    private String provinceCode;

    @JsonProperty("is_active")
    @JsonAlias("isActive")
    private Boolean isActive;
}
