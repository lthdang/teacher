package com.teacher.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teacher.dto.admin.AdminLoginRequest;
import com.teacher.dto.admin.AdminRequestDTO;
import com.teacher.dto.permission.CreatePermissionItemRequest;
import com.teacher.dto.permission.DeletePermissionsRequest;
import com.teacher.dto.permission.UpdatePermissionRequest;
import com.teacher.dto.permission.UpdateSubAdminPermissionsRequest;
import com.teacher.entity.Admin;
import com.teacher.entity.AdminType;
import com.teacher.entity.Permission;
import com.teacher.repository.IAdminRepository;
import com.teacher.repository.IPermissionRepository;
import com.teacher.security.JwtService;

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

    @Autowired
    private IPermissionRepository permissionRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private MockMvc mockMvc;
    private Admin superAdmin;
    private Admin subAdmin;
    private String superAdminToken;
    private String subAdminToken;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        // Ensure Super Admin exists
        superAdmin = adminRepository.findByEmailAndIsDeletedFalse("lthdang@ninepoints.vn")
                .orElseGet(() -> {
                    Admin admin = Admin.builder()
                            .email("lthdang@ninepoints.vn")
                            .password(passwordEncoder.encode("Password123!"))
                            .type(AdminType.SUPER_ADMIN)
                            .surname("Super")
                            .firstName("Admin")
                            .isDeleted(false)
                            .build();
                    admin.setCreatedAt(now);
                    admin.setUpdatedAt(now);
                    return adminRepository.save(admin);
                });
        superAdminToken = jwtService.generateToken(superAdmin.getId(), superAdmin.getEmail());

        // Ensure a Sub Admin exists
        String subAdminEmail = "subadmin_test@ninepoints.vn";
        subAdmin = adminRepository.findByEmailAndIsDeletedFalse(subAdminEmail)
                .orElseGet(() -> {
                    Admin admin = Admin.builder()
                            .email(subAdminEmail)
                            .password(passwordEncoder.encode("Password123!"))
                            .type(AdminType.SUB_ADMIN)
                            .surname("Sub")
                            .firstName("Admin")
                            .isDeleted(false)
                            .build();
                    admin.setCreatedAt(now);
                    admin.setUpdatedAt(now);
                    return adminRepository.save(admin);
                });
        subAdminToken = jwtService.generateToken(subAdmin.getId(), subAdmin.getEmail());
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
    @DisplayName("POST /api/admin/login - Valid Super Admin login returns token and permissions array")
    void testLoginSuperAdminSuccess() throws Exception {
        AdminLoginRequest request = new AdminLoginRequest("lthdang@ninepoints.vn", "Password123!");

        mockMvc.perform(post("/api/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.admin.type").value("SUPER_ADMIN"))
                .andExpect(jsonPath("$.permissions").isArray());
    }

    @Test
    @DisplayName("POST /api/admin/register - Super Admin can register sub-admin with type = SUB_ADMIN")
    void testRegisterAdminBySuperAdminSuccess() throws Exception {
        String uniqueEmail = "newsubadmin_" + UUID.randomUUID() + "@test.com";
        AdminRequestDTO request = AdminRequestDTO.builder()
                .email(uniqueEmail)
                .surname("Doe")
                .firstName("John")
                .password("SecurePass123!")
                .build();

        mockMvc.perform(post("/api/admin/register")
                .header("Authorization", "Bearer " + superAdminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(uniqueEmail))
                .andExpect(jsonPath("$.type").value("SUB_ADMIN"));
    }

    @Test
    @DisplayName("POST /api/admin/register - Unauthenticated request returns 401 Unauthorized")
    void testRegisterAdminUnauthenticated() throws Exception {
        AdminRequestDTO request = AdminRequestDTO.builder()
                .email("unauth_" + UUID.randomUUID() + "@test.com")
                .surname("Doe")
                .firstName("John")
                .password("SecurePass123!")
                .build();

        mockMvc.perform(post("/api/admin/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/admin/register - Sub Admin without permission returns 403 Forbidden")
    void testRegisterAdminBySubAdminForbidden() throws Exception {
        AdminRequestDTO request = AdminRequestDTO.builder()
                .email("forbidden_" + UUID.randomUUID() + "@test.com")
                .surname("Forbidden")
                .firstName("Sub")
                .password("SecurePass123!")
                .build();

        mockMvc.perform(post("/api/admin/register")
                .header("Authorization", "Bearer " + subAdminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    @DisplayName("GET /api/admin/sub-admins - Super Admin can retrieve list of sub-admins")
    void testGetSubAdminsBySuperAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/sub-admins")
                .header("Authorization", "Bearer " + superAdminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/admin/sub-admins/{id} - Super Admin gets sub-admin details with permissions")
    void testGetSubAdminDetailSuccess() throws Exception {
        mockMvc.perform(get("/api/admin/sub-admins/" + subAdmin.getId())
                .header("Authorization", "Bearer " + superAdminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(subAdmin.getId().toString()))
                .andExpect(jsonPath("$.type").value("SUB_ADMIN"))
                .andExpect(jsonPath("$.permissions").isArray());
    }

    @Test
    @DisplayName("GET /api/admin/sub-admins/{id} - Invalid or Non-SUB_ADMIN ID returns 404 Not Found")
    void testGetSubAdminDetailNotFound() throws Exception {
        mockMvc.perform(get("/api/admin/sub-admins/" + UUID.randomUUID())
                .header("Authorization", "Bearer " + superAdminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/admin/sub-admins/{id} - Super Admin soft deletes sub-admin")
    void testDeleteSubAdminSuccess() throws Exception {
        String email = "to_delete_" + UUID.randomUUID() + "@test.com";
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        Admin adminToDelete = Admin.builder()
                .email(email)
                .password(passwordEncoder.encode("Password123!"))
                .type(AdminType.SUB_ADMIN)
                .surname("Delete")
                .firstName("Me")
                .isDeleted(false)
                .build();
        adminToDelete.setCreatedAt(now);
        adminToDelete.setUpdatedAt(now);
        Admin toDelete = adminRepository.save(adminToDelete);

        mockMvc.perform(delete("/api/admin/sub-admins/" + toDelete.getId())
                .header("Authorization", "Bearer " + superAdminToken))
                .andExpect(status().isOk());

        // Verify soft deleted
        Admin fetched = adminRepository.findById(toDelete.getId()).orElseThrow();
        org.junit.jupiter.api.Assertions.assertTrue(fetched.getIsDeleted());
    }

    @Test
    @DisplayName("DELETE /api/admin/sub-admins/{id} - Attempt to delete SUPER_ADMIN returns 400 Bad Request")
    void testDeleteSuperAdminBadRequest() throws Exception {
        mockMvc.perform(delete("/api/admin/sub-admins/" + superAdmin.getId())
                .header("Authorization", "Bearer " + superAdminToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/admin/sub-admins/{id}/permissions - Full replace permissions with invalid ID returns 400")
    void testReplaceSubAdminPermissionsInvalidId() throws Exception {
        UpdateSubAdminPermissionsRequest request = new UpdateSubAdminPermissionsRequest(List.of(999999L));

        mockMvc.perform(put("/api/admin/sub-admins/" + subAdmin.getId() + "/permissions")
                .header("Authorization", "Bearer " + superAdminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/permissions - Retrieve all system permissions")
    void testGetAllPermissions() throws Exception {
        mockMvc.perform(get("/api/permissions")
                .header("Authorization", "Bearer " + superAdminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("POST /api/permissions - Create permissions with duplicate code returns 400")
    void testCreatePermissionsDuplicateCode() throws Exception {
        CreatePermissionItemRequest item1 = CreatePermissionItemRequest.builder()
                .name("Dup Test")
                .permissionCode("permission.duplicate_test_code")
                .endpoint("/api/dup")
                .build();
        CreatePermissionItemRequest item2 = CreatePermissionItemRequest.builder()
                .name("Dup Test 2")
                .permissionCode("permission.duplicate_test_code")
                .endpoint("/api/dup2")
                .build();

        mockMvc.perform(post("/api/permissions")
                .header("Authorization", "Bearer " + superAdminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(item1, item2))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/permissions - Batch delete permissions with notFoundIds")
    void testDeletePermissions() throws Exception {
        Permission p = permissionRepository.save(Permission.builder()
                .name("Temp Permission")
                .permissionCode("permission.temp_" + UUID.randomUUID())
                .endpoint("/api/temp")
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build());

        DeletePermissionsRequest request = new DeletePermissionsRequest(List.of(p.getId(), 999999L));

        mockMvc.perform(delete("/api/permissions")
                .header("Authorization", "Bearer " + superAdminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deletedPermissions").isArray())
                .andExpect(jsonPath("$.notFoundIds").isArray());
    }

    @Test
    @DisplayName("PUT /api/permissions/{id} - Update permission success")
    void testUpdatePermissionSuccess() throws Exception {
        Permission p = permissionRepository.save(Permission.builder()
                .name("Old Permission Name")
                .permissionCode("permission.to_update_" + UUID.randomUUID())
                .endpoint("/api/old-endpoint")
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build());

        UpdatePermissionRequest updateReq = UpdatePermissionRequest.builder()
                .name("Updated Permission Name")
                .permissionCode("permission.updated_" + UUID.randomUUID())
                .endpoint("/api/new-endpoint")
                .build();

        mockMvc.perform(put("/api/permissions/" + p.getId())
                .header("Authorization", "Bearer " + superAdminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Permission Name"))
                .andExpect(jsonPath("$.endpoint").value("/api/new-endpoint"));
    }

    @Test
    @DisplayName("PUT /api/permissions/{id} - Non-existent permission ID returns 404")
    void testUpdatePermissionNotFound() throws Exception {
        UpdatePermissionRequest updateReq = UpdatePermissionRequest.builder()
                .name("Updated Name")
                .permissionCode("permission.test_code_" + UUID.randomUUID())
                .endpoint("/api/test")
                .build();

        mockMvc.perform(put("/api/permissions/999999")
                .header("Authorization", "Bearer " + superAdminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isNotFound());
    }
}
