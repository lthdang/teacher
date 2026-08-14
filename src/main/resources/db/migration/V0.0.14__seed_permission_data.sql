-- =============================================================================
-- V0.0.14 — Seed Permission Data
-- Inserts initial permission catalog records into the permission table.
-- =============================================================================

INSERT INTO permission (name, permission_code, endpoint, created_at, updated_at)
VALUES
    ('Update Admin Info', 'permission.update_admin_info', '/api/admin/profile', NOW(), NOW()),
    ('View Sub-Admins', 'permission.view_sub_admins', '/api/admin/sub-admins', NOW(), NOW()),
    ('Create Sub-Admin', 'permission.create_sub_admin', '/api/admin/register', NOW(), NOW()),
    ('Change Password', 'permission.change_password', '/api/admin/password', NOW(), NOW())
ON CONFLICT (permission_code) DO NOTHING;
