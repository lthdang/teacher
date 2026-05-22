-- =============================================================================
-- V0.0.2 — Create Tenants Table
-- Level 1: Multi-tenant foundation
-- Each tenant represents a school in the multi-tenant architecture
-- =============================================================================

CREATE TABLE tenants (
    id            UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    name          VARCHAR(255) NOT NULL,
    -- URL-friendly identifier used in routing and API calls
    slug          VARCHAR(100) NOT NULL UNIQUE,
    -- Controls feature enablement based on grade level
    school_level  school_level NOT NULL,
    -- Province code per Ministry of Education standard (e.g., 01 = Hanoi, 79 = HCM)
    province_code CHAR(2),
    -- Flexible per-school configuration (logo, theme, timezone, school_year_start, etc.)
    settings      JSONB        NOT NULL DEFAULT '{}',
    -- Soft delete: retains data even after contract expiry
    is_active     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- Index for fast slug lookups (used in routing)
CREATE INDEX idx_tenants_slug ON tenants (slug);

-- Index for filtering active tenants
CREATE INDEX idx_tenants_is_active ON tenants (is_active);
