-- =============================================================================
-- V0.0.7 — Create User Career Ranks Table
-- Level 1: Professional rank history for users (system-level, cross-tenant)
-- Tracks the progression of a user's professional role over time.
-- Only one career rank is active per user at any time (is_current = true).
-- When a new rank is granted, the previous one is superseded.
-- =============================================================================

CREATE TABLE user_career_ranks (
    id                                UUID        PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id                           UUID        NOT NULL,
    role_id                           UUID        NOT NULL,
    -- Tenant that granted this career rank to the user
    granted_by_tenant_id              UUID        NOT NULL,
    -- User (admin/manager) who performed the granting action
    granted_by_user_id                UUID        NOT NULL,
    granted_at                        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    -- Optional justification for the rank change
    granted_reason                    TEXT,
    -- true = this is the user's current active career rank
    -- false = superseded by a newer career rank
    is_current                        BOOLEAN     NOT NULL DEFAULT TRUE,
    -- Timestamp when this rank was replaced by a new one
    superseded_at                     TIMESTAMPTZ,
    -- Reference to the career rank record that replaced this one
    superseded_by_user_career_rank_id UUID,
    created_at                        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    update_at                         TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_ucr_user
        FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_ucr_role
        FOREIGN KEY (role_id) REFERENCES roles (id),
    CONSTRAINT fk_ucr_granted_by_tenant
        FOREIGN KEY (granted_by_tenant_id) REFERENCES tenants (id),
    CONSTRAINT fk_ucr_granted_by_user
        FOREIGN KEY (granted_by_user_id) REFERENCES users (id),
    CONSTRAINT fk_ucr_superseded_by
        FOREIGN KEY (superseded_by_user_career_rank_id) REFERENCES user_career_ranks (id)
);

-- Index for retrieving all career ranks of a user
CREATE INDEX idx_ucr_user_id ON user_career_ranks (user_id);

-- Index for finding the current active rank per user
CREATE INDEX idx_ucr_user_is_current ON user_career_ranks (user_id, is_current);

-- Enforce only one current career rank per user
CREATE UNIQUE INDEX idx_ucr_single_current ON user_career_ranks (user_id)
    WHERE is_current = TRUE;
