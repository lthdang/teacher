-- =============================================================================
-- V0.0.3 — Create Users Table
-- Level 1: Core identity — global login accounts across all tenants
-- A user belongs to the system globally; tenant association is managed
-- through user_tenant_roles.
-- =============================================================================

CREATE TABLE users (
    id            UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    -- Unique login email across the entire system
    email         VARCHAR(255) NOT NULL UNIQUE,
    -- BCrypt-hashed password (never store plaintext)
    password_hash VARCHAR(255) NOT NULL,
    full_name     VARCHAR(255) NOT NULL,
    phone         VARCHAR(20),
    -- Controls user access; new accounts start as Pending
    status        user_status  NOT NULL DEFAULT 'Pending',
    last_login_at TIMESTAMPTZ,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    update_at     TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- Index for fast email lookups during authentication
CREATE INDEX idx_users_email ON users (email);

-- Index for filtering by status (e.g., active users only)
CREATE INDEX idx_users_status ON users (status);
