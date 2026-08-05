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
import com.teachermanagement.teacher_management.dto.admin.AdminRequestDTO;
import com.teachermanagement.teacher_management.repository.IAdminRepository;
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

    @Autowired
    private IAdminRepository adminRepository;

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

    @Test
    @DisplayName("POST /api/admin/register - Valid request should create admin and return 200 OK")
    void testRegisterAdminSuccess() throws Exception {
        String uniqueEmail = "newadmin_" + UUID.randomUUID() + "@test.com";
        AdminRequestDTO request = AdminRequestDTO.builder()
                .email(uniqueEmail)
                .surname("Doe")
                .firstName("John")
                .password("SecurePass123!")
                .build();

        mockMvc.perform(post("/api/admin/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(uniqueEmail))
                .andExpect(jsonPath("$.surname").value("Doe"))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    @DisplayName("POST /api/admin/register - Invalid data should return 400 Bad Request")
    void testRegisterAdminInvalidData() throws Exception {
        AdminRequestDTO request = AdminRequestDTO.builder()
                .email("invalid-email")
                .surname("")
                .firstName("")
                .password("123")
                .build();

        mockMvc.perform(post("/api/admin/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/admin/register - Duplicate email should return 400 Bad Request")
    void testRegisterAdminDuplicateEmail() throws Exception {
        String email = "duplicate_" + UUID.randomUUID() + "@test.com";
        AdminRequestDTO request = AdminRequestDTO.builder()
                .email(email)
                .surname("Smith")
                .firstName("Alice")
                .password("Password123!")
                .build();

        // First registration
        mockMvc.perform(post("/api/admin/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Duplicate registration attempt
        mockMvc.perform(post("/api/admin/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("ERROR_EMAIL_EXISTED"));
    }
}
