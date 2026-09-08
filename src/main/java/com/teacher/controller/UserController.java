package com.teacher.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teacher.dto.user.CreateUserRequest;
import com.teacher.dto.user.UpdateUserRequest;
import com.teacher.dto.user.UserDTO;
import com.teacher.dto.user.UserLoginRequest;
import com.teacher.dto.user.UserLoginResponse;
import com.teacher.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping({"/user", "/api/user"})
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * POST /user
     * Register / create a new user.
     */
    @PostMapping
    public UserDTO createUser(@Valid @RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }

    /**
     * PUT /user/{userId}
     * Update an existing user's details (phone, password, full_name).
     */
    @PutMapping("/{userId}")
    public UserDTO updateUser(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateUserRequest request) {
        return userService.updateUser(userId, request);
    }

    /**
     * GET /user/{userId}
     * Retrieve user details by ID.
     */
    @GetMapping("/{userId}")
    public UserDTO getUserById(@PathVariable UUID userId) {
        return userService.getUserById(userId);
    }

    /**
     * POST /user/login
     * Authenticate user with email and password.
     */
    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@Valid @RequestBody UserLoginRequest request) {
        UserLoginResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /user/sign-in
     * Alias endpoint for user login.
     */
    @PostMapping("/sign-in")
    public ResponseEntity<UserLoginResponse> signIn(@Valid @RequestBody UserLoginRequest request) {
        UserLoginResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /user/logout
     * Stateless logout; the client should discard the token.
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            try {
                UUID userId = UUID.fromString(authentication.getName());
                userService.logout(userId);
            } catch (IllegalArgumentException ignored) {
                // Ignore if authentication name is not a valid UUID
            }
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /user/sign-out
     * Alias endpoint for user logout.
     */
    @PostMapping("/sign-out")
    public ResponseEntity<Void> signOut(Authentication authentication) {
        return logout(authentication);
    }
}
