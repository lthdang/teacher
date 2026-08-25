-- =============================================================================
-- V0.0.16 — Seed Role Permissions
-- Inserts role management permissions into the permission catalog.
-- =============================================================================

INSERT INTO permission (name, permission_code, endpoint, created_at, updated_at)
VALUES
    ('View Roles', 'permission.view_roles', '/api/roles', NOW(), NOW()),
    ('Create Role', 'permission.create_role', '/api/roles', NOW(), NOW()),
    ('Update Role', 'permission.update_role', '/api/roles', NOW(), NOW()),
    ('Delete Role', 'permission.delete_role', '/api/roles', NOW(), NOW())
ON CONFLICT (permission_code) DO NOTHING;
