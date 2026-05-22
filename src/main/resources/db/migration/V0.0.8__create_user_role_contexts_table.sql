-- =============================================================================
-- V0.0.8 — Create User Role Contexts Table
-- Level 1: Scoped role assignments within a tenant
-- Binds a user_tenant_role to a specific operational context
-- (e.g., a particular class, department, or project).
-- One user_tenant_role can have multiple context entries for different scopes.
-- =============================================================================

CREATE TABLE user_role_contexts (
    id                  UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    -- The tenant role assignment this context applies to
    user_tenant_role_id UUID         NOT NULL,
    -- Category of scope (e.g., "teaching", "administration", "counseling")
    context_type        VARCHAR(255) NOT NULL,
    -- Identifier of the specific resource in that context
    -- (e.g., a class ID, department ID, or project ID)
    context_id          VARCHAR(255) NOT NULL,
    -- Optional free-text note about this context assignment
    note                TEXT,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    update_at           TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_urc_user_tenant_role
        FOREIGN KEY (user_tenant_role_id) REFERENCES user_tenant_roles (id) ON DELETE CASCADE
);

-- Index for finding all contexts for a given role assignment
CREATE INDEX idx_urc_user_tenant_role_id ON user_role_contexts (user_tenant_role_id);

-- Index for querying by context type (e.g., all "teaching" assignments)
CREATE INDEX idx_urc_context_type ON user_role_contexts (context_type);

-- Index for resolving which role assignments cover a specific resource
CREATE INDEX idx_urc_context_type_id ON user_role_contexts (context_type, context_id);
