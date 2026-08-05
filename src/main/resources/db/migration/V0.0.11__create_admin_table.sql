-- =============================================================================
-- V0.0.11 — Create Admin Table
-- Separates system administrator identity from the general user table.
-- Admin accounts are managed independently and authenticate via JWT.
-- =============================================================================

CREATE TABLE admin (
    id         UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    -- Unique login email for the system administrator
    email      VARCHAR(255) NOT NULL UNIQUE,
    -- BCrypt-hashed password (never store plaintext)
    password   VARCHAR(255) NOT NULL,
    surname    VARCHAR(100),
    first_name VARCHAR(100),
    -- URL or path to avatar image
    avatar     VARCHAR(500),
    last_login TIMESTAMPTZ,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- Index for fast email lookups during authentication
CREATE INDEX idx_admin_email ON admin (email);
