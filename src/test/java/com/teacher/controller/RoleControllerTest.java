package com.teacher.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
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
import com.teacher.dto.role.CreateRoleRequest;
import com.teacher.dto.role.UpdateRoleRequest;
import com.teacher.entity.Admin;
import com.teacher.entity.AdminPermission;
import com.teacher.entity.AdminType;
import com.teacher.entity.Department;
import com.teacher.entity.Permission;
import com.teacher.entity.Role;
import com.teacher.entity.Tenant;
import com.teacher.entity.User;
import com.teacher.entity.UserTenantRole;
import com.teacher.repository.IAdminPermissionRepository;
import com.teacher.repository.IAdminRepository;
import com.teacher.repository.IPermissionRepository;
import com.teacher.repository.IRoleRepository;
import com.teacher.repository.IUserTenantRoleRepository;
import com.teacher.security.JwtService;

import jakarta.persistence.EntityManager;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.Mockito.when;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("default")
class RoleControllerTest {

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
    private IAdminPermissionRepository adminPermissionRepository;

    @Autowired
    private IRoleRepository roleRepository;

    @MockitoBean
    private IUserTenantRoleRepository userTenantRoleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private EntityManager entityManager;

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
        superAdmin = adminRepository.findByEmailAndIsDeletedFalse("superadmin_role_test@ninepoints.vn")
                .orElseGet(() -> {
                    Admin admin = Admin.builder()
                            .email("superadmin_role_test@ninepoints.vn")
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
        subAdmin = adminRepository.findByEmailAndIsDeletedFalse("subadmin_role_test@ninepoints.vn")
                .orElseGet(() -> {
                    Admin admin = Admin.builder()
                            .email("subadmin_role_test@ninepoints.vn")
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
        adminPermissionRepository.deleteAll(adminPermissionRepository.findByAdminId(subAdmin.getId()));
        subAdminToken = jwtService.generateToken(subAdmin.getId(), subAdmin.getEmail());
    }

    @Test
    @DisplayName("GET /api/roles - Paginated response returns 200 OK and SearchResponseDTO")
    void testGetRolesPagination() throws Exception {
        mockMvc.perform(get("/api/roles")
                .header("Authorization", "Bearer " + superAdminToken)
                .param("page", "0")
                .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.limit").value(10))
                .andExpect(jsonPath("$.totalRecords").exists())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("GET /api/roles - Search keyword filters results correctly")
    void testGetRolesWithSearch() throws Exception {
        String uniqueCode = "SEARCH_ROLE_" + UUID.randomUUID().toString().substring(0, 8);
        Role role = Role.builder()
                .code(uniqueCode)
                .name("Special Unique Search Role")
                .hierarchyLevel(5)
                .permissions("{}")
                .isSystemRole(false)
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .updateAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build();
        roleRepository.save(role);

        mockMvc.perform(get("/api/roles")
                .header("Authorization", "Bearer " + superAdminToken)
                .param("search", uniqueCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].code").value(uniqueCode))
                .andExpect(jsonPath("$.totalRecords").value(1));
    }

    @Test
    @DisplayName("GET /api/roles/{id} - Get role by ID success")
    void testGetRoleByIdSuccess() throws Exception {
        String uniqueCode = "GET_ROLE_" + UUID.randomUUID().toString().substring(0, 8);
        Role role = roleRepository.save(Role.builder()
                .code(uniqueCode)
                .name("Get By Id Test Role")
                .hierarchyLevel(3)
                .permissions("{\"teacher.view\": true}")
                .isSystemRole(false)
                .description("Role description")
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .updateAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build());

        mockMvc.perform(get("/api/roles/" + role.getId())
                .header("Authorization", "Bearer " + superAdminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(role.getId().toString()))
                .andExpect(jsonPath("$.code").value(uniqueCode))
                .andExpect(jsonPath("$.name").value("Get By Id Test Role"))
                .andExpect(jsonPath("$.hierarchyLevel").value(3))
                .andExpect(jsonPath("$.isSystemRole").value(false));
    }

    @Test
    @DisplayName("GET /api/roles/{id} - Non-existent ID returns 404 Not Found")
    void testGetRoleByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/roles/" + UUID.randomUUID())
                .header("Authorization", "Bearer " + superAdminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/roles - Create custom role success")
    void testCreateRoleSuccess() throws Exception {
        String uniqueCode = "CREATE_ROLE_" + UUID.randomUUID().toString().substring(0, 8);
        CreateRoleRequest request = CreateRoleRequest.builder()
                .code(uniqueCode)
                .name("New Created Role")
                .hierarchyLevel(10)
                .permissions(Map.of("student.view", true, "student.edit", true))
                .isSystemRole(false)
                .description("Newly created custom role description")
                .build();

        mockMvc.perform(post("/api/roles")
                .header("Authorization", "Bearer " + superAdminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.code").value(uniqueCode))
                .andExpect(jsonPath("$.name").value("New Created Role"))
                .andExpect(jsonPath("$.hierarchyLevel").value(10))
                .andExpect(jsonPath("$.isSystemRole").value(false));
    }

    @Test
    @DisplayName("POST /api/roles - Duplicate code returns 400 Bad Request")
    void testCreateRoleDuplicateCode() throws Exception {
        String uniqueCode = "DUP_ROLE_" + UUID.randomUUID().toString().substring(0, 8);
        roleRepository.save(Role.builder()
                .code(uniqueCode)
                .name("Initial Role")
                .hierarchyLevel(1)
                .permissions("{}")
                .isSystemRole(false)
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .updateAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build());

        CreateRoleRequest request = CreateRoleRequest.builder()
                .code(uniqueCode)
                .name("Another Role with Same Code")
                .hierarchyLevel(2)
                .build();

        mockMvc.perform(post("/api/roles")
                .header("Authorization", "Bearer " + superAdminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("POST /api/roles - Missing required fields returns 400 Bad Request")
    void testCreateRoleValidationFailure() throws Exception {
        CreateRoleRequest request = CreateRoleRequest.builder()
                .code("")
                .name("")
                .build();

        mockMvc.perform(post("/api/roles")
                .header("Authorization", "Bearer " + superAdminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/roles/{id} - Update role details success")
    void testUpdateRoleSuccess() throws Exception {
        String uniqueCode = "UP_ROLE_" + UUID.randomUUID().toString().substring(0, 8);
        Role role = roleRepository.save(Role.builder()
                .code(uniqueCode)
                .name("Old Role Name")
                .hierarchyLevel(1)
                .permissions("{}")
                .isSystemRole(false)
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .updateAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build());

        UpdateRoleRequest request = UpdateRoleRequest.builder()
                .name("Updated Role Name")
                .hierarchyLevel(8)
                .permissions("{\"updated.perm\": true}")
                .description("Updated description")
                .build();

        mockMvc.perform(put("/api/roles/" + role.getId())
                .header("Authorization", "Bearer " + superAdminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(role.getId().toString()))
                .andExpect(jsonPath("$.name").value("Updated Role Name"))
                .andExpect(jsonPath("$.hierarchyLevel").value(8))
                .andExpect(jsonPath("$.description").value("Updated description"));
    }

    @Test
    @DisplayName("PUT /api/roles/{id} - Non-existent ID returns 404 Not Found")
    void testUpdateRoleNotFound() throws Exception {
        UpdateRoleRequest request = UpdateRoleRequest.builder()
                .name("Updated Name")
                .build();

        mockMvc.perform(put("/api/roles/" + UUID.randomUUID())
                .header("Authorization", "Bearer " + superAdminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/roles/{id} - Delete custom unassigned role success")
    void testDeleteRoleSuccess() throws Exception {
        String uniqueCode = "DEL_ROLE_" + UUID.randomUUID().toString().substring(0, 8);
        Role role = roleRepository.save(Role.builder()
                .code(uniqueCode)
                .name("To Delete Role")
                .hierarchyLevel(1)
                .permissions("{}")
                .isSystemRole(false)
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .updateAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build());

        mockMvc.perform(delete("/api/roles/" + role.getId())
                .header("Authorization", "Bearer " + superAdminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Role deleted successfully."));

        mockMvc.perform(get("/api/roles/" + role.getId())
                .header("Authorization", "Bearer " + superAdminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/roles/{id} - Attempt to delete system role returns 400 Bad Request")
    void testDeleteSystemRoleBadRequest() throws Exception {
        String uniqueCode = "SYS_ROLE_" + UUID.randomUUID().toString().substring(0, 8);
        Role role = roleRepository.save(Role.builder()
                .code(uniqueCode)
                .name("System Role Immutable")
                .hierarchyLevel(100)
                .permissions("{}")
                .isSystemRole(true)
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .updateAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build());

        mockMvc.perform(delete("/api/roles/" + role.getId())
                .header("Authorization", "Bearer " + superAdminToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("DELETE /api/roles/{id} - Attempt to delete role assigned to users returns 400 Bad Request")
    void testDeleteRoleInUseBadRequest() throws Exception {
        String uniqueCode = "ASSIGNED_ROLE_" + UUID.randomUUID().toString().substring(0, 8);
        Role role = roleRepository.save(Role.builder()
                .code(uniqueCode)
                .name("Assigned Role")
                .hierarchyLevel(2)
                .permissions("{}")
                .isSystemRole(false)
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .updateAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build());

        when(userTenantRoleRepository.existsByRoleId(role.getId())).thenReturn(true);

        mockMvc.perform(delete("/api/roles/" + role.getId())
                .header("Authorization", "Bearer " + superAdminToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("GET /api/roles - Unauthenticated request returns 401 Unauthorized")
    void testGetRolesUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("GET /api/roles - Sub Admin without permission returns 403 Forbidden (Permission Denied)")
    void testGetRolesSubAdminWithoutPermission() throws Exception {
        mockMvc.perform(get("/api/roles")
                .header("Authorization", "Bearer " + subAdminToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/roles - Sub Admin with permission.view_roles returns 200 OK")
    void testGetRolesSubAdminWithPermission() throws Exception {
        Permission permission = permissionRepository.findByPermissionCode("permission.view_roles")
                .orElseGet(() -> permissionRepository.save(Permission.builder()
                        .name("View Roles")
                        .permissionCode("permission.view_roles")
                        .endpoint("/api/roles")
                        .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                        .updatedAt(OffsetDateTime.now(ZoneOffset.UTC))
                        .build()));

        adminPermissionRepository.save(AdminPermission.builder()
                .adminId(subAdmin.getId())
                .permissionId(permission.getId())
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build());

        mockMvc.perform(get("/api/roles")
                .header("Authorization", "Bearer " + subAdminToken))
                .andExpect(status().isOk());
    }
}
