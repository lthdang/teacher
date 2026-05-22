-- =============================================================================
-- V0.0.9 — Seed System Roles
-- Level 1: Initial RBAC role definitions
-- These are system-managed roles (is_system_role = true) and cannot be
-- deleted or modified by tenants. Permissions use a dot-notation key model.
-- Hierarchy levels: higher number = greater authority.
-- =============================================================================

INSERT INTO roles (id, code, name, hierarchy_level, permissions, is_system_role, description)
VALUES
    -- Level 1 — Teacher: Basic instructional access
    (
        uuid_generate_v4(),
        'TEACHER',
        'Giáo Viên',
        1,
        '{
            "teacher.view":             true,
            "teacher.edit_own":         true,
            "class.view":               true,
            "department.view":          false,
            "department.manage":        false,
            "user.manage":              false,
            "role.manage":              false,
            "tenant.manage":            false
        }',
        TRUE,
        'Standard teacher role. Can view class information and manage their own profile.'
    ),

    -- Level 2 — Subject Deputy Head: Partial leadership within a department
    (
        uuid_generate_v4(),
        'DEPUTY_SUBJECT_HEAD',
        'Phó Bộ Môn',
        2,
        '{
            "teacher.view":             true,
            "teacher.edit_own":         true,
            "class.view":               true,
            "department.view":          true,
            "department.manage":        false,
            "user.manage":              false,
            "role.manage":              false,
            "tenant.manage":            false
        }',
        TRUE,
        'Deputy head of a subject department. Can view department information.'
    ),

    -- Level 3 — Subject Head: Manages their department
    (
        uuid_generate_v4(),
        'SUBJECT_HEAD',
        'Trưởng Bộ Môn',
        3,
        '{
            "teacher.view":             true,
            "teacher.edit_own":         true,
            "class.view":               true,
            "department.view":          true,
            "department.manage":        true,
            "user.manage":              false,
            "role.manage":              false,
            "tenant.manage":            false
        }',
        TRUE,
        'Head of a subject department. Can manage their department and its members.'
    ),

    -- Level 4 — HR Admin: Manages users and role assignments within a tenant
    (
        uuid_generate_v4(),
        'HR_ADMIN',
        'Quản Trị Nhân Sự',
        4,
        '{
            "teacher.view":             true,
            "teacher.edit_own":         true,
            "class.view":               true,
            "department.view":          true,
            "department.manage":        true,
            "user.manage":              true,
            "role.manage":              false,
            "tenant.manage":            false
        }',
        TRUE,
        'Human Resources Administrator. Can manage users, departments, and role assignments within their tenant.'
    ),

    -- Level 5 — Vice Principal: Assists the principal in school administration
    (
        uuid_generate_v4(),
        'VICE_PRINCIPAL',
        'Phó Hiệu Trưởng',
        5,
        '{
            "teacher.view":             true,
            "teacher.edit_own":         true,
            "class.view":               true,
            "department.view":          true,
            "department.manage":        true,
            "user.manage":              true,
            "role.manage":              true,
            "tenant.manage":            false
        }',
        TRUE,
        'Vice Principal. Assists the principal and has broad administrative rights within the tenant.'
    ),

    -- Level 6 — Principal: Full authority within their tenant
    (
        uuid_generate_v4(),
        'PRINCIPAL',
        'Hiệu Trưởng',
        6,
        '{
            "teacher.view":             true,
            "teacher.edit_own":         true,
            "class.view":               true,
            "department.view":          true,
            "department.manage":        true,
            "user.manage":              true,
            "role.manage":              true,
            "tenant.manage":            true
        }',
        TRUE,
        'Principal. Has full administrative authority over the entire tenant (school).'
    );
