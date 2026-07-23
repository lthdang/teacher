package com.teachermanagement.teacher_management.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teachermanagement.teacher_management.dto.response.UserResponse;
import com.teachermanagement.teacher_management.service.AdminService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    /**
     * GET /api/admin/system-admin
     *
     * Verifies that the system administrator user seeded by migration V0.0.10 exists.
     * Returns the user info on success (password hash is never included in the response).
     */
    @GetMapping("/system-admin")
    public ResponseEntity<UserResponse> getSystemAdmin() {
        UserResponse response = adminService.getSystemAdminUser();
        return ResponseEntity.ok(response);
    }
}
