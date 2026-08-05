package com.teacher.dto.admin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class AdminRequestDTO {

    @NotBlank(message = "ERROR_EMAIL_IS_REQUIRED")
    @Email(message = "ERROR_EMAIL_INVALID")
    private String email;

    @NotBlank(message = "ERROR_LASTNAME_IS_REQUIRED")
    @Size(max = 100, message = "ERROR_SURNAME_TOO_LONG")
    private String surname;

    @NotBlank(message = "ERROR_FIRSTNAME_IS_REQUIRED")
    @Size(max = 100, message = "ERROR_FIRSTNAME_TOO_LONG")
    private String firstName;

    @NotBlank(message = "ERROR_PASSWORD_IS_REQUIRED")
    @Size(min = 6, max = 100, message = "TOO_SHORT")
    private String password;
}
