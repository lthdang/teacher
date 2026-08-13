package com.teacher.common.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    private int status;           // HTTP status code: 404, 400, 500...
    private String error;         // Erro name: "Not Found", "Bad Request"...
    private String code;          // Erro name: "Not Found", "Bad Request"...
    private String message;       // Detailed error message: "Teacher with id 1 not found"
    private String path;          // Error Endpoint: "/test"

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}
