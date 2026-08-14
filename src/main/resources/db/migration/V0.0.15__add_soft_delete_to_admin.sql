-- =============================================================================
-- V0.0.15 — Add Soft Delete to Admin Table
-- Supports soft deletion of sub-admin accounts.
-- =============================================================================

ALTER TABLE admin ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE admin ADD COLUMN deleted_at TIMESTAMPTZ;

CREATE INDEX idx_admin_is_deleted ON admin (is_deleted);
