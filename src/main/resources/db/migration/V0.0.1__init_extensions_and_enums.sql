-- =============================================================================
-- V0.0.1 — Init Extensions and Enums
-- Level 1: Multi-tenant & Access Control foundation
-- =============================================================================

-- Enable UUID generation support
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- -----------------------------------------------------------------------------
-- ENUM: school_level
-- Used in: tenants
-- Controls which features are enabled per grade level
-- -----------------------------------------------------------------------------
CREATE TYPE school_level AS ENUM (
    'primary',
    'secondary',
    'university'
);

-- -----------------------------------------------------------------------------
-- ENUM: user_status
-- Used in: users
-- Controls user access and activity within the system
-- -----------------------------------------------------------------------------
CREATE TYPE user_status AS ENUM (
    'Active',
    'Inactive',
    'Blocked',
    'Pending'
);
