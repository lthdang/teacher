-- =============================================================================
-- V0.0.13 — Add Admin Type and Permissions
-- Distinguishes access rights between SUPER_ADMIN and SUB_ADMIN.
-- Creates permission and admin_permission tables.
-- =============================================================================

-- 1. Add type column to admin table
ALTER TABLE admin ADD COLUMN type VARCHAR(20) NOT NULL DEFAULT 'SUB_ADMIN';

-- 2. Update default super admin account
UPDATE admin SET type = 'SUPER_ADMIN' WHERE email = 'lthdang@ninepoints.vn';

-- 3. Create permission catalog table
CREATE TABLE permission (
    id              BIGSERIAL    PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    permission_code VARCHAR(100) NOT NULL UNIQUE,
    endpoint        VARCHAR(255) NOT NULL,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- 4. Create admin_permission junction table
CREATE TABLE admin_permission (
    admin_id      UUID        NOT NULL REFERENCES admin(id) ON DELETE CASCADE,
    permission_id BIGINT      NOT NULL REFERENCES permission(id) ON DELETE CASCADE,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (admin_id, permission_id)
);

-- 5. Indexes for fast permission lookups
CREATE INDEX idx_admin_type ON admin (type);
CREATE INDEX idx_permission_code ON permission (permission_code);
CREATE INDEX idx_admin_permission_admin_id ON admin_permission (admin_id);
CREATE INDEX idx_admin_permission_permission_id ON admin_permission (permission_id);
