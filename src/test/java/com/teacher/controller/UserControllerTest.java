package com.teacher.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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
import com.teacher.dto.user.CreateUserRequest;
import com.teacher.dto.user.UpdateUserRequest;
import com.teacher.dto.user.UserLoginRequest;
import com.teacher.entity.User;
import com.teacher.entity.User.UserStatus;
import com.teacher.repository.IUserRepository;
import com.teacher.security.JwtService;

@SpringBootTest
@ActiveProfiles("default")
class UserControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    // =========================================================================
    // POST /user (Registration / Create User)
    // =========================================================================

    @Test
    @DisplayName("POST /user - Successful user creation with default Active status")
    void testCreateUserSuccess() throws Exception {
        String uniqueEmail = "user-" + UUID.randomUUID() + "@test.com";
        CreateUserRequest request = CreateUserRequest.builder()
                .email(uniqueEmail)
                .password("Secret123!")
                .fullName("John Doe")
                .phone("0912345678")
                .build();

        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value(uniqueEmail))
                .andExpect(jsonPath("$.full_name").value("John Doe"))
                .andExpect(jsonPath("$.phone").value("0912345678"))
                .andExpect(jsonPath("$.status").value("Active"))
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.update_at").exists());

        User userInDb = userRepository.findByEmail(uniqueEmail).orElseThrow();
        assertEquals("John Doe", userInDb.getFullName());
        assertEquals(UserStatus.Active, userInDb.getStatus());
        assertTrue(passwordEncoder.matches("Secret123!", userInDb.getPasswordHash()));
    }

    @Test
    @DisplayName("POST /api/user - Alternate prefix creates user successfully")
    void testCreateUserApiPrefixSuccess() throws Exception {
        String uniqueEmail = "user-api-" + UUID.randomUUID() + "@test.com";
        CreateUserRequest request = CreateUserRequest.builder()
                .email(uniqueEmail)
                .password("Secret123!")
                .fullName("API User")
                .phone("0987654321")
                .build();

        mockMvc.perform(post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(uniqueEmail))
                .andExpect(jsonPath("$.full_name").value("API User"));
    }

    @Test
    @DisplayName("POST /user - Duplicate email returns 400 ERROR_EMAIL_EXISTED")
    void testCreateUserDuplicateEmail() throws Exception {
        String uniqueEmail = "dup-" + UUID.randomUUID() + "@test.com";
        userRepository.save(User.builder()
                .email(uniqueEmail)
                .passwordHash(passwordEncoder.encode("Password123!"))
                .fullName("Existing User")
                .status(UserStatus.Active)
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .updateAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build());

        CreateUserRequest duplicateRequest = CreateUserRequest.builder()
                .email(uniqueEmail)
                .password("NewPassword123!")
                .fullName("Duplicate Attempt")
                .build();

        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("ERROR_EMAIL_EXISTED"));
    }

    @Test
    @DisplayName("POST /user - Invalid email returns 400")
    void testCreateUserInvalidEmail() throws Exception {
        CreateUserRequest request = CreateUserRequest.builder()
                .email("not-an-email")
                .password("Password123!")
                .fullName("Invalid Email User")
                .build();

        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /user - Password shorter than 6 characters returns 400")
    void testCreateUserShortPassword() throws Exception {
        CreateUserRequest request = CreateUserRequest.builder()
                .email("short-pass-" + UUID.randomUUID() + "@test.com")
                .password("123")
                .fullName("Short Pass User")
                .build();

        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /user - Blank full name returns 400")
    void testCreateUserBlankFullName() throws Exception {
        CreateUserRequest request = CreateUserRequest.builder()
                .email("no-name-" + UUID.randomUUID() + "@test.com")
                .password("Password123!")
                .fullName("   ")
                .build();

        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // =========================================================================
    // PUT /user/{userId} (Update User)
    // =========================================================================

    @Test
    @DisplayName("PUT /user/{userId} - Successfully updates full name, phone, and password")
    void testUpdateUserSuccess() throws Exception {
        String email = "update-" + UUID.randomUUID() + "@test.com";
        User originalUser = userRepository.save(User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode("OldPassword123!"))
                .fullName("Original Name")
                .phone("0111111111")
                .status(UserStatus.Active)
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .updateAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build());

        UpdateUserRequest updateRequest = UpdateUserRequest.builder()
                .fullName("Updated Name")
                .phone("0222222222")
                .password("NewPassword456!")
                .build();

        mockMvc.perform(put("/user/" + originalUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(originalUser.getId().toString()))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.full_name").value("Updated Name"))
                .andExpect(jsonPath("$.phone").value("0222222222"))
                .andExpect(jsonPath("$.status").value("Active"));

        User refreshed = userRepository.findById(originalUser.getId()).orElseThrow();
        assertEquals("Updated Name", refreshed.getFullName());
        assertEquals("0222222222", refreshed.getPhone());
        assertTrue(passwordEncoder.matches("NewPassword456!", refreshed.getPasswordHash()));
        assertEquals(email, refreshed.getEmail()); // Email remains unchanged
        assertEquals(UserStatus.Active, refreshed.getStatus()); // Status remains unchanged
    }

    @Test
    @DisplayName("PUT /user/{userId} - Updating non-existent user returns 404")
    void testUpdateUserNotFound() throws Exception {
        UUID randomId = UUID.randomUUID();
        UpdateUserRequest request = UpdateUserRequest.builder()
                .fullName("Does Not Matter")
                .phone("0999999999")
                .build();

        mockMvc.perform(put("/user/" + randomId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /user/{userId} - Invalid password (< 6 chars) returns 400")
    void testUpdateUserInvalidPassword() throws Exception {
        User user = userRepository.save(User.builder()
                .email("pass-val-" + UUID.randomUUID() + "@test.com")
                .passwordHash(passwordEncoder.encode("OldPassword123!"))
                .fullName("Pass Val")
                .status(UserStatus.Active)
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .updateAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build());

        UpdateUserRequest request = UpdateUserRequest.builder()
                .password("123")
                .build();

        mockMvc.perform(put("/user/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // =========================================================================
    // GET /user/{userId} (Retrieve User Details)
    // =========================================================================

    @Test
    @DisplayName("GET /user/{userId} - Successfully retrieves user details")
    void testGetUserByIdSuccess() throws Exception {
        String email = "get-" + UUID.randomUUID() + "@test.com";
        User user = userRepository.save(User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode("Password123!"))
                .fullName("Alice Wonderland")
                .phone("0333333333")
                .status(UserStatus.Active)
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .updateAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build());

        mockMvc.perform(get("/user/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId().toString()))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.full_name").value("Alice Wonderland"))
                .andExpect(jsonPath("$.phone").value("0333333333"))
                .andExpect(jsonPath("$.status").value("Active"));
    }

    @Test
    @DisplayName("GET /user/{userId} - Non-existent ID returns 404")
    void testGetUserByIdNotFound() throws Exception {
        mockMvc.perform(get("/user/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    // =========================================================================
    // POST /user/login and POST /user/sign-in (Authentication)
    // =========================================================================

    @Test
    @DisplayName("POST /user/login - Successful login returns valid JWT token and user info")
    void testUserLoginSuccess() throws Exception {
        String email = "login-" + UUID.randomUUID() + "@test.com";
        User user = userRepository.save(User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode("CorrectPassword123!"))
                .fullName("Login User")
                .phone("0444444444")
                .status(UserStatus.Active)
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .updateAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build());

        UserLoginRequest loginRequest = UserLoginRequest.builder()
                .email(email)
                .password("CorrectPassword123!")
                .build();

        mockMvc.perform(post("/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token_type").value("Bearer"))
                .andExpect(jsonPath("$.expires_at").exists())
                .andExpect(jsonPath("$.user.id").value(user.getId().toString()))
                .andExpect(jsonPath("$.user.email").value(email));

        User refreshed = userRepository.findById(user.getId()).orElseThrow();
        assertNotNull(refreshed.getLastLoginAt());
    }

    @Test
    @DisplayName("POST /user/sign-in - Sign-in alias works identically to login")
    void testUserSignInAliasSuccess() throws Exception {
        String email = "signin-" + UUID.randomUUID() + "@test.com";
        userRepository.save(User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode("CorrectPassword123!"))
                .fullName("Sign-in User")
                .status(UserStatus.Active)
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .updateAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build());

        UserLoginRequest loginRequest = UserLoginRequest.builder()
                .email(email)
                .password("CorrectPassword123!")
                .build();

        mockMvc.perform(post("/user/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.email").value(email));
    }

    @Test
    @DisplayName("POST /user/login - Incorrect password returns 400")
    void testUserLoginIncorrectPassword() throws Exception {
        String email = "wrong-pass-" + UUID.randomUUID() + "@test.com";
        userRepository.save(User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode("RealPassword123!"))
                .fullName("Wrong Pass")
                .status(UserStatus.Active)
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .updateAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build());

        UserLoginRequest loginRequest = UserLoginRequest.builder()
                .email(email)
                .password("WrongPassword999!")
                .build();

        mockMvc.perform(post("/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("ERROR_USERNAME_PASSWORD_INVALID"));
    }

    @Test
    @DisplayName("POST /user/login - Non-existent user returns 400")
    void testUserLoginNonExistentUser() throws Exception {
        UserLoginRequest loginRequest = UserLoginRequest.builder()
                .email("ghost-" + UUID.randomUUID() + "@test.com")
                .password("DoesNotMatter123!")
                .build();

        mockMvc.perform(post("/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("ERROR_USERNAME_PASSWORD_INVALID"));
    }

    @Test
    @DisplayName("POST /user/login - Inactive / Blocked user returns 400 ERROR_USER_IS_NOT_AVAILABLE")
    void testUserLoginInactiveUser() throws Exception {
        String email = "blocked-" + UUID.randomUUID() + "@test.com";
        userRepository.save(User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode("Password123!"))
                .fullName("Blocked User")
                .status(UserStatus.Blocked)
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .updateAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build());

        UserLoginRequest loginRequest = UserLoginRequest.builder()
                .email(email)
                .password("Password123!")
                .build();

        mockMvc.perform(post("/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("ERROR_USER_IS_NOT_AVAILABLE"));
    }

    // =========================================================================
    // POST /user/logout and POST /user/sign-out (Logout)
    // =========================================================================

    @Test
    @DisplayName("POST /user/logout - Successful logout returns 204 No Content")
    void testUserLogoutSuccess() throws Exception {
        String email = "logout-" + UUID.randomUUID() + "@test.com";
        User user = userRepository.save(User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode("Password123!"))
                .fullName("Logout User")
                .status(UserStatus.Active)
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .updateAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build());

        String token = jwtService.generateToken(user.getId(), user.getEmail());

        mockMvc.perform(post("/user/logout")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("POST /api/user/logout - Alternate prefix logout returns 204 No Content")
    void testUserLogoutApiPrefixSuccess() throws Exception {
        String email = "logout-api-" + UUID.randomUUID() + "@test.com";
        User user = userRepository.save(User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode("Password123!"))
                .fullName("Logout API User")
                .status(UserStatus.Active)
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .updateAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build());

        String token = jwtService.generateToken(user.getId(), user.getEmail());

        mockMvc.perform(post("/api/user/logout")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("POST /user/sign-out - Sign-out alias returns 204 No Content")
    void testUserSignOutAliasSuccess() throws Exception {
        String email = "signout-" + UUID.randomUUID() + "@test.com";
        User user = userRepository.save(User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode("Password123!"))
                .fullName("Sign-out User")
                .status(UserStatus.Active)
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .updateAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build());

        String token = jwtService.generateToken(user.getId(), user.getEmail());

        mockMvc.perform(post("/user/sign-out")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("POST /user/logout - Logout without token returns 204 No Content (stateless)")
    void testUserLogoutWithoutToken() throws Exception {
        mockMvc.perform(post("/user/logout"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("POST /user/logout - Blocked user returns 400 ERROR_USER_IS_NOT_AVAILABLE")
    void testUserLogoutBlockedUser() throws Exception {
        String email = "logout-blocked-" + UUID.randomUUID() + "@test.com";
        User user = userRepository.save(User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode("Password123!"))
                .fullName("Blocked Logout User")
                .status(UserStatus.Blocked)
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .updateAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build());

        String token = jwtService.generateToken(user.getId(), user.getEmail());

        mockMvc.perform(post("/user/logout")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("ERROR_USER_IS_NOT_AVAILABLE"));
    }

    @Test
    @DisplayName("POST /user/logout - Non-existent user returns 404")
    void testUserLogoutNonExistentUser() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        String token = jwtService.generateToken(nonExistentId, "ghost@test.com");

        mockMvc.perform(post("/user/logout")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}
