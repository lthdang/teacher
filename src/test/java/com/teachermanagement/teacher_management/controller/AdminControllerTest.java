package com.teachermanagement.teacher_management.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teachermanagement.teacher_management.dto.admin.AdminLoginRequest;
import com.teachermanagement.teacher_management.security.JwtService;

@SpringBootTest
@ActiveProfiles("default")
class AdminControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    private MockMvc mockMvc;
    private String validToken;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        validToken = jwtService.generateToken(UUID.randomUUID(), "admin@test.com");
    }

    @Test
    @DisplayName("OPTIONS /api/admin/profile - CORS preflight should return 200 OK")
    void testCorsPreflight() throws Exception {
        mockMvc.perform(options("/api/admin/profile")
                .header("Access-Control-Request-Method", "GET")
                .header("Origin", "http://localhost:3000"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"));
    }

    @Test
    @DisplayName("GET /api/admin/profile - Unauthenticated request should return 401 Unauthorized")
    void testGetProfileUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/admin/profile"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    @DisplayName("GET /api/admin/profile - Request with invalid token should return 401 Unauthorized")
    void testGetProfileInvalidToken() throws Exception {
        mockMvc.perform(get("/api/admin/profile")
                .header("Authorization", "Bearer invalid.token.str"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("POST /api/admin/login - Invalid credentials should return 400 Bad Request")
    void testLoginInvalidCredentials() throws Exception {
        AdminLoginRequest request = new AdminLoginRequest("wrong@email.com", "wrongpassword");

        mockMvc.perform(post("/api/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
