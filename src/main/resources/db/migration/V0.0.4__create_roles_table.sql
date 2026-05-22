-- =============================================================================
-- V0.0.4 — Create Roles Table
-- Level 1: RBAC — Role definitions shared across the system
-- Roles can be system-managed (is_system_role = true, immutable by tenants)
-- or tenant-created (is_system_role = false, customizable per tenant).
-- =============================================================================

CREATE TABLE roles (
    id              UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    -- Short machine-readable code (e.g., TEACHER, PRINCIPAL, HR_ADMIN)
    code            VARCHAR(50)  NOT NULL UNIQUE,
    -- Human-readable display name (e.g., Giáo Viên, Hiệu Trưởng)
    name            VARCHAR(255) NOT NULL,
    -- Numeric hierarchy for access control decisions (higher = more authority)
    hierarchy_level INTEGER      NOT NULL,
    -- Granular permission flags stored as JSON object
    -- e.g., { "teacher.view": true, "teacher.edit": true, "department.manage": false }
    permissions     JSONB        NOT NULL DEFAULT '{}',
    -- true  = system role managed by developers (cannot be deleted or edited by tenants)
    -- false = custom role created by a tenant
    is_system_role  BOOLEAN      NOT NULL DEFAULT FALSE,
    description     TEXT,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    update_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- Index for fast code lookups
CREATE INDEX idx_roles_code ON roles (code);

-- Index for filtering system vs. tenant roles
CREATE INDEX idx_roles_is_system_role ON roles (is_system_role);

-- Index for hierarchy-based queries (e.g., find all roles with level >= N)
CREATE INDEX idx_roles_hierarchy_level ON roles (hierarchy_level);
