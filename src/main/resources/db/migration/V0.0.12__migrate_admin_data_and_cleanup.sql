-- =============================================================================
-- V0.0.12 — Migrate Admin Data and Cleanup
-- Copies the seeded system administrator account from the users table into the
-- new admin table, then removes it from users so admin identity is fully
-- separated from general user accounts.
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 1. Copy admin record from users → admin
--    Maps: password_hash → password, full_name → first_name, update_at → updated_at
-- -----------------------------------------------------------------------------
INSERT INTO admin (id, email, password, surname, first_name, avatar, last_login, created_at, updated_at)
SELECT
    id,
    email,
    password_hash,
    NULL,
    full_name,
    NULL,
    last_login_at,
    created_at,
    update_at
FROM users
WHERE email = 'lthdang@ninepoints.vn';

-- -----------------------------------------------------------------------------
-- 2. Remove admin record from users table
-- -----------------------------------------------------------------------------
DELETE FROM users WHERE email = 'lthdang@ninepoints.vn';
