-- =============================================================================
-- V0.0.10 — Seed System Admin Role and User
-- Level 1: Bootstrap system-level administrator account
-- Adds a SYSTEM_ADMIN role (hierarchy_level = 7, above PRINCIPAL = 6) and
-- seeds the initial system administrator user with a BCrypt-hashed password.
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 1. Insert SYSTEM_ADMIN role
-- Highest authority role — spans all tenants and manages system-wide settings.
-- -----------------------------------------------------------------------------
INSERT INTO roles (id, code, name, hierarchy_level, permissions, is_system_role, description)
VALUES (
    uuid_generate_v4(),
    'SYSTEM_ADMIN',
    'Quản Trị Hệ Thống',
    7,
    '{
        "teacher.view":         true,
        "teacher.edit_own":     true,
        "class.view":           true,
        "department.view":      true,
        "department.manage":    true,
        "user.manage":          true,
        "role.manage":          true,
        "tenant.manage":        true,
        "system.manage":        true
    }',
    TRUE,
    'System administrator with full access across all tenants and system-level management.'
);

-- -----------------------------------------------------------------------------
-- 2. Insert admin user
-- username (full_name): admin
-- email:               lthdang@ninepoints.vn
-- password:            admin@123  (BCrypt $2a$10$, cost=10)
-- phone:               0987654321
-- status:              Active
-- -----------------------------------------------------------------------------
INSERT INTO users (id, email, password_hash, full_name, phone, status, created_at, update_at)
VALUES (
    uuid_generate_v4(),
    'lthdang@ninepoints.vn',
    '$2a$10$8O5wX2QgU6WBW5/sLy7lSON0Np7JjcCa59KDG.hw7BP0GFgKKzaSa',
    'admin',
    '0987654321',
    'Active',
    NOW(),
    NOW()
);
