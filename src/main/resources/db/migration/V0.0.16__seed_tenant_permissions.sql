-- =============================================================================
-- V0.0.16 — Seed Tenant Permissions
-- Inserts tenant management permissions into the permission catalog.
-- =============================================================================

INSERT INTO permission (name, permission_code, endpoint, created_at, updated_at)
VALUES
    ('View Tenants', 'permission.view_tenants', '/api/tenants', NOW(), NOW()),
    ('Create Tenant', 'permission.create_tenant', '/api/tenants', NOW(), NOW()),
    ('Update Tenant', 'permission.update_tenant', '/api/tenants', NOW(), NOW()),
    ('Delete Tenant', 'permission.delete_tenant', '/api/tenants', NOW(), NOW())
ON CONFLICT (permission_code) DO NOTHING;
