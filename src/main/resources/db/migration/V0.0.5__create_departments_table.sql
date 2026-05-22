-- =============================================================================
-- V0.0.5 — Create Departments Table
-- Level 1: Organizational structure within a tenant
-- Departments are scoped to a tenant; manager is a user within the system.
-- =============================================================================

CREATE TABLE departments (
    id              UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    -- Tenant this department belongs to (multi-tenant scoping)
    tenant_id       UUID         NOT NULL,
    -- Short department code for quick reference (e.g., "HR", "FIN", "ENG")
    code            VARCHAR(50)  NOT NULL,
    -- Full department name (e.g., "Phòng Nhân Sự")
    name            VARCHAR(255) NOT NULL,
    -- Detailed description of the department's function
    description     TEXT,
    -- User who manages this department (nullable: department may have no manager yet)
    manager_user_id UUID,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    update_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_departments_tenant
        FOREIGN KEY (tenant_id) REFERENCES tenants (id),
    CONSTRAINT fk_departments_manager
        FOREIGN KEY (manager_user_id) REFERENCES users (id)
);

-- Department codes must be unique within a tenant
CREATE UNIQUE INDEX idx_departments_tenant_code ON departments (tenant_id, code);

-- Index for listing departments by tenant
CREATE INDEX idx_departments_tenant_id ON departments (tenant_id);

-- Index for resolving manager lookups
CREATE INDEX idx_departments_manager_user_id ON departments (manager_user_id);
