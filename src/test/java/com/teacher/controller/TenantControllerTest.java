package com.teacher.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teacher.dto.tenant.CreateTenantRequest;
import com.teacher.dto.tenant.UpdateTenantRequest;
import com.teacher.entity.Admin;
import com.teacher.entity.AdminPermission;
import com.teacher.entity.AdminType;
import com.teacher.entity.Permission;
import com.teacher.entity.Tenant;
import com.teacher.entity.Tenant.SchoolLevel;
import com.teacher.repository.IAdminPermissionRepository;
import com.teacher.repository.IAdminRepository;
import com.teacher.repository.IPermissionRepository;
import com.teacher.repository.ITenantRepository;
import com.teacher.repository.IUserTenantRoleRepository;
import com.teacher.security.JwtService;

@SpringBootTest
@ActiveProfiles("default")
class TenantControllerTest {

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
    private ITenantRepository tenantRepository;

    @MockitoBean
    private IUserTenantRoleRepository userTenantRoleRepository;

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
        superAdmin = adminRepository.findByEmailAndIsDeletedFalse("superadmin_tenant_test@ninepoints.vn")
                .orElseGet(() -> {
                    Admin admin = Admin.builder()
                            .email("superadmin_tenant_test@ninepoints.vn")
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
        subAdmin = adminRepository.findByEmailAndIsDeletedFalse("subadmin_tenant_test@ninepoints.vn")
                .orElseGet(() -> {
                    Admin admin = Admin.builder()
                            .email("subadmin_tenant_test@ninepoints.vn")
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
    @DisplayName("GET /tenants - Returns list of tenants as JSON array")
    void testGetTenants() throws Exception {
        String uniqueSlug = "slug-list-" + UUID.randomUUID().toString().substring(0, 8);
        tenantRepository.save(Tenant.builder()
                .name("List Test School")
                .slug(uniqueSlug)
                .schoolLevel(SchoolLevel.primary)
                .provinceCode("01")
                .settings("{}")
                .isActive(true)
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build());

        mockMvc.perform(get("/tenants")
                .header("Authorization", "Bearer " + superAdminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/tenants - Alternate prefix returns list of tenants as JSON array")
    void testGetTenantsApiPrefix() throws Exception {
        mockMvc.perform(get("/api/tenants")
                .header("Authorization", "Bearer " + superAdminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /tenants - Search keyword filters tenants correctly")
    void testGetTenantsWithSearch() throws Exception {
        String uniqueSlug = "slug-search-" + UUID.randomUUID().toString().substring(0, 8);
        Tenant tenant = tenantRepository.save(Tenant.builder()
                .name("Unique Searchable School Name")
                .slug(uniqueSlug)
                .schoolLevel(SchoolLevel.secondary)
                .provinceCode("79")
                .settings("{}")
                .isActive(true)
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build());

        mockMvc.perform(get("/tenants")
                .header("Authorization", "Bearer " + superAdminToken)
                .param("search", uniqueSlug))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].slug").value(uniqueSlug))
                .andExpect(jsonPath("$[0].name").value("Unique Searchable School Name"));
    }

    @Test
    @DisplayName("GET /tenants/{tenantId} - Get tenant details by ID success")
    void testGetTenantByIdSuccess() throws Exception {
        String uniqueSlug = "slug-get-" + UUID.randomUUID().toString().substring(0, 8);
        Tenant tenant = tenantRepository.save(Tenant.builder()
                .name("Get Details School")
                .slug(uniqueSlug)
                .schoolLevel(SchoolLevel.university)
                .provinceCode("48")
                .settings("{\"theme\": \"dark\"}")
                .isActive(true)
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build());

        mockMvc.perform(get("/tenants/" + tenant.getId())
                .header("Authorization", "Bearer " + superAdminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(tenant.getId().toString()))
                .andExpect(jsonPath("$.name").value("Get Details School"))
                .andExpect(jsonPath("$.slug").value(uniqueSlug))
                .andExpect(jsonPath("$.school_level").value("university"))
                .andExpect(jsonPath("$.province_code").value("48"))
                .andExpect(jsonPath("$.settings").value("{\"theme\":\"dark\"}"))
                .andExpect(jsonPath("$.is_active").value(true));
    }

    @Test
    @DisplayName("GET /tenants/{tenantId} - Non-existent ID returns 404 Not Found")
    void testGetTenantByIdNotFound() throws Exception {
        mockMvc.perform(get("/tenants/" + UUID.randomUUID())
                .header("Authorization", "Bearer " + superAdminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /tenants - Create tenant success")
    void testCreateTenantSuccess() throws Exception {
        String uniqueSlug = "slug-create-" + UUID.randomUUID().toString().substring(0, 8);
        CreateTenantRequest request = CreateTenantRequest.builder()
                .name("Created High School")
                .slug(uniqueSlug)
                .schoolLevel(SchoolLevel.secondary)
                .provinceCode("01")
                .settings(Map.of("timezone", "Asia/Ho_Chi_Minh"))
                .isActive(true)
                .build();

        mockMvc.perform(post("/tenants")
                .header("Authorization", "Bearer " + superAdminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Created High School"))
                .andExpect(jsonPath("$.slug").value(uniqueSlug))
                .andExpect(jsonPath("$.school_level").value("secondary"))
                .andExpect(jsonPath("$.province_code").value("01"))
                .andExpect(jsonPath("$.is_active").value(true));
    }

    @Test
    @DisplayName("POST /tenants - Missing required fields returns 400 Bad Request")
    void testCreateTenantValidationFailure() throws Exception {
        CreateTenantRequest request = CreateTenantRequest.builder()
                .name("")
                .slug(null)
                .schoolLevel(null)
                .build();

        mockMvc.perform(post("/tenants")
                .header("Authorization", "Bearer " + superAdminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("POST /tenants - Duplicate slug returns 400 Bad Request")
    void testCreateTenantDuplicateSlug() throws Exception {
        String duplicateSlug = "slug-dup-" + UUID.randomUUID().toString().substring(0, 8);
        tenantRepository.save(Tenant.builder()
                .name("First School")
                .slug(duplicateSlug)
                .schoolLevel(SchoolLevel.primary)
                .provinceCode("01")
                .settings("{}")
                .isActive(true)
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build());

        CreateTenantRequest request = CreateTenantRequest.builder()
                .name("Second School")
                .slug(duplicateSlug)
                .schoolLevel(SchoolLevel.primary)
                .provinceCode("01")
                .build();

        mockMvc.perform(post("/tenants")
                .header("Authorization", "Bearer " + superAdminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("ERROR_TENANT_SLUG_EXISTED"));
    }

    @Test
    @DisplayName("PUT /tenants/{tenantId} - Update tenant details success")
    void testUpdateTenantSuccess() throws Exception {
        String initialSlug = "slug-update-" + UUID.randomUUID().toString().substring(0, 8);
        Tenant tenant = tenantRepository.save(Tenant.builder()
                .name("Original School Name")
                .slug(initialSlug)
                .schoolLevel(SchoolLevel.primary)
                .provinceCode("01")
                .settings("{}")
                .isActive(true)
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build());

        UpdateTenantRequest updateRequest = UpdateTenantRequest.builder()
                .name("Updated School Name")
                .schoolLevel(SchoolLevel.secondary)
                .provinceCode("79")
                .settings(Map.of("academic_year", "2026-2027"))
                .isActive(false)
                .build();

        mockMvc.perform(put("/tenants/" + tenant.getId())
                .header("Authorization", "Bearer " + superAdminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(tenant.getId().toString()))
                .andExpect(jsonPath("$.name").value("Updated School Name"))
                .andExpect(jsonPath("$.school_level").value("secondary"))
                .andExpect(jsonPath("$.province_code").value("79"))
                .andExpect(jsonPath("$.is_active").value(false));
    }

    @Test
    @DisplayName("PUT /tenants/{tenantId} - Duplicate slug collision returns 400 Bad Request")
    void testUpdateTenantSlugCollision() throws Exception {
        String existingSlug = "slug-exist-" + UUID.randomUUID().toString().substring(0, 8);
        tenantRepository.save(Tenant.builder()
                .name("Other School")
                .slug(existingSlug)
                .schoolLevel(SchoolLevel.primary)
                .provinceCode("01")
                .settings("{}")
                .isActive(true)
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build());

        String targetSlug = "slug-target-" + UUID.randomUUID().toString().substring(0, 8);
        Tenant targetTenant = tenantRepository.save(Tenant.builder()
                .name("Target School")
                .slug(targetSlug)
                .schoolLevel(SchoolLevel.primary)
                .provinceCode("01")
                .settings("{}")
                .isActive(true)
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build());

        UpdateTenantRequest request = UpdateTenantRequest.builder()
                .slug(existingSlug)
                .build();

        mockMvc.perform(put("/tenants/" + targetTenant.getId())
                .header("Authorization", "Bearer " + superAdminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("ERROR_TENANT_SLUG_EXISTED"));
    }

    @Test
    @DisplayName("DELETE /tenants/{tenantId} - Delete tenant success")
    void testDeleteTenantSuccess() throws Exception {
        String uniqueSlug = "slug-del-" + UUID.randomUUID().toString().substring(0, 8);
        Tenant tenant = tenantRepository.save(Tenant.builder()
                .name("Delete Candidate School")
                .slug(uniqueSlug)
                .schoolLevel(SchoolLevel.primary)
                .provinceCode("01")
                .settings("{}")
                .isActive(true)
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build());

        when(userTenantRoleRepository.existsByTenantId(tenant.getId())).thenReturn(false);

        mockMvc.perform(delete("/tenants/" + tenant.getId())
                .header("Authorization", "Bearer " + superAdminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Tenant deleted successfully."));
    }

    @Test
    @DisplayName("DELETE /tenants/{tenantId} - Attempt to delete tenant in use returns 400 Bad Request")
    void testDeleteTenantInUseBadRequest() throws Exception {
        String uniqueSlug = "slug-inuse-" + UUID.randomUUID().toString().substring(0, 8);
        Tenant tenant = tenantRepository.save(Tenant.builder()
                .name("In Use School")
                .slug(uniqueSlug)
                .schoolLevel(SchoolLevel.primary)
                .provinceCode("01")
                .settings("{}")
                .isActive(true)
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .updatedAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build());

        when(userTenantRoleRepository.existsByTenantId(tenant.getId())).thenReturn(true);

        mockMvc.perform(delete("/tenants/" + tenant.getId())
                .header("Authorization", "Bearer " + superAdminToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("ERROR_TENANT_IN_USE"));
    }

    @Test
    @DisplayName("GET /tenants - Unauthenticated request returns 401 Unauthorized")
    void testGetTenantsUnauthenticated() throws Exception {
        mockMvc.perform(get("/tenants"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("GET /tenants - Sub Admin without permission returns 403 Forbidden")
    void testGetTenantsSubAdminWithoutPermission() throws Exception {
        mockMvc.perform(get("/tenants")
                .header("Authorization", "Bearer " + subAdminToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /tenants - Sub Admin with permission.view_tenants returns 200 OK")
    void testGetTenantsSubAdminWithPermission() throws Exception {
        Permission permission = permissionRepository.findByPermissionCode("permission.view_tenants")
                .orElseGet(() -> permissionRepository.save(Permission.builder()
                        .name("View Tenants")
                        .permissionCode("permission.view_tenants")
                        .endpoint("/api/tenants")
                        .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                        .updatedAt(OffsetDateTime.now(ZoneOffset.UTC))
                        .build()));

        adminPermissionRepository.save(AdminPermission.builder()
                .adminId(subAdmin.getId())
                .permissionId(permission.getId())
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build());

        mockMvc.perform(get("/tenants")
                .header("Authorization", "Bearer " + subAdminToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /tenants - Sub Admin without permission returns 403 Forbidden")
    void testCreateTenantSubAdminWithoutPermission() throws Exception {
        CreateTenantRequest request = CreateTenantRequest.builder()
                .name("Forbidden School")
                .slug("slug-forbidden-" + UUID.randomUUID().toString().substring(0, 8))
                .schoolLevel(SchoolLevel.primary)
                .build();

        mockMvc.perform(post("/tenants")
                .header("Authorization", "Bearer " + subAdminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /tenants - Sub Admin with permission.create_tenant returns 200 OK")
    void testCreateTenantSubAdminWithPermission() throws Exception {
        Permission permission = permissionRepository.findByPermissionCode("permission.create_tenant")
                .orElseGet(() -> permissionRepository.save(Permission.builder()
                        .name("Create Tenant")
                        .permissionCode("permission.create_tenant")
                        .endpoint("/api/tenants")
                        .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                        .updatedAt(OffsetDateTime.now(ZoneOffset.UTC))
                        .build()));

        adminPermissionRepository.save(AdminPermission.builder()
                .adminId(subAdmin.getId())
                .permissionId(permission.getId())
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build());

        String uniqueSlug = "slug-allowed-" + UUID.randomUUID().toString().substring(0, 8);
        CreateTenantRequest request = CreateTenantRequest.builder()
                .name("Allowed School")
                .slug(uniqueSlug)
                .schoolLevel(SchoolLevel.primary)
                .provinceCode("01")
                .build();

        mockMvc.perform(post("/tenants")
                .header("Authorization", "Bearer " + subAdminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value(uniqueSlug));
    }
}
