-- =============================================================================
-- V0.0.6 — Create User Tenant Roles Table
-- Level 1: RBAC — User operating rights at each tenant
-- A user can hold multiple roles at the same tenant; one role is marked primary.
-- Roles are time-bounded (valid_from / valid_until) and support revocation audit.
-- =============================================================================

CREATE TABLE user_tenant_roles (
    id                     UUID        PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id                UUID        NOT NULL,
    role_id                UUID        NOT NULL,
    tenant_id              UUID        NOT NULL,
    -- true = primary role for this user at this tenant; only one per user+tenant
    is_primary             BOOLEAN     NOT NULL DEFAULT FALSE,
    -- true = currently active; false = revoked or expired
    is_active              BOOLEAN     NOT NULL DEFAULT TRUE,
    -- Role becomes effective from this timestamp
    valid_from             TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    -- Role expires at this timestamp (null = does not expire)
    valid_until            TIMESTAMPTZ,
    -- User who granted this role
    granted_by_user_id     UUID        NOT NULL,
    -- Populated when the role is revoked
    deactivated_at         TIMESTAMPTZ,
    deactivated_by_user_id UUID,
    deactivation_reason    TEXT,
    created_at             TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    update_at              TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_utr_user
        FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_utr_role
        FOREIGN KEY (role_id) REFERENCES roles (id),
    CONSTRAINT fk_utr_tenant
        FOREIGN KEY (tenant_id) REFERENCES tenants (id),
    CONSTRAINT fk_utr_granted_by
        FOREIGN KEY (granted_by_user_id) REFERENCES users (id),
    CONSTRAINT fk_utr_deactivated_by
        FOREIGN KEY (deactivated_by_user_id) REFERENCES users (id)
);

-- Index for loading all roles of a user at a specific tenant
CREATE INDEX idx_utr_user_tenant ON user_tenant_roles (user_id, tenant_id);

-- Index for finding all users holding a specific role at a tenant
CREATE INDEX idx_utr_role_tenant ON user_tenant_roles (role_id, tenant_id);

-- Index for filtering active roles
CREATE INDEX idx_utr_is_active ON user_tenant_roles (is_active);

-- Enforce only one primary role per user per tenant
CREATE UNIQUE INDEX idx_utr_primary_role ON user_tenant_roles (user_id, tenant_id)
    WHERE is_primary = TRUE AND is_active = TRUE;
