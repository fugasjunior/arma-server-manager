CREATE TABLE permissions
(
    code        VARCHAR(64)  NOT NULL,
    description VARCHAR(255) NOT NULL,
    PRIMARY KEY (code)
);

CREATE TABLE roles
(
    id          BIGINT      NOT NULL AUTO_INCREMENT,
    name        VARCHAR(64) NOT NULL,
    description VARCHAR(255),
    built_in    BOOLEAN     NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    UNIQUE KEY uq_roles_name (name)
);

CREATE TABLE role_permissions
(
    role_id         BIGINT      NOT NULL,
    permission_code VARCHAR(64) NOT NULL,
    PRIMARY KEY (role_id, permission_code),
    CONSTRAINT fk_rp_role FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE,
    CONSTRAINT fk_rp_perm FOREIGN KEY (permission_code) REFERENCES permissions (code) ON DELETE CASCADE
);

CREATE TABLE users
(
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    username            VARCHAR(64)  NOT NULL,
    password_hash       VARCHAR(255) NOT NULL,
    enabled             BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    password_changed_at TIMESTAMP NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_users_username (username)
);

CREATE TABLE user_roles
(
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_ur_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_ur_role FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE RESTRICT
);

-- Seed permissions
INSERT INTO permissions (code, description)
VALUES ('SERVER_VIEW', 'View servers and their status'),
       ('SERVER_OPERATE', 'Start, stop, and restart servers'),
       ('SERVER_MODIFY', 'Create and edit servers'),
       ('SERVER_DELETE', 'Delete servers'),
       ('SERVER_SECRETS_VIEW', 'View server passwords and admin passwords'),
       ('MOD_VIEW', 'View workshop mods and presets'),
       ('MOD_MODIFY', 'Add, update, and edit mods and presets'),
       ('MOD_DELETE', 'Delete mods and presets'),
       ('SCENARIO_VIEW', 'View scenarios'),
       ('SCENARIO_MODIFY', 'Upload scenarios'),
       ('SCENARIO_DELETE', 'Delete scenarios'),
       ('INSTALL_VIEW', 'View server installation status'),
       ('INSTALL_MANAGE', 'Install, update, and uninstall server binaries'),
       ('STEAM_AUTH_ADMIN', 'Manage Steam credentials and view SteamCmd logs'),
       ('ADDITIONAL_SERVER_VIEW', 'View additional servers (Minecraft, etc.)'),
       ('ADDITIONAL_SERVER_OPERATE', 'Start and stop additional servers'),
       ('SYSTEM_VIEW', 'View system resource information'),
       ('USER_ADMIN', 'Manage users and roles'),
       ('SERVER_LOGS_VIEW', 'View server log files');


-- Seed built-in roles
INSERT INTO roles (name, description, built_in)
VALUES ('Admin', 'Full access to all features', TRUE),
       ('Operator', 'Start/stop servers, view mods/scenarios, view system information', TRUE),
       ('Viewer', 'Read-only access to all areas', TRUE);

-- Admin: all permissions
INSERT INTO role_permissions (role_id, permission_code)
SELECT r.id, p.code
FROM roles r,
     permissions p
WHERE r.name = 'Admin';

-- Operator permissions
INSERT INTO role_permissions (role_id, permission_code)
SELECT r.id, p.code
FROM roles r,
     permissions p
WHERE r.name = 'Operator'
  AND p.code IN (
                 'SERVER_VIEW',
                 'SERVER_OPERATE',
                 'MOD_VIEW',
                 'SCENARIO_VIEW',
                 'INSTALL_VIEW',
                 'ADDITIONAL_SERVER_VIEW',
                 'ADDITIONAL_SERVER_OPERATE',
                 'SYSTEM_VIEW',
                 'SERVER_LOGS_VIEW'
    );

-- Viewer: all VIEW permissions
INSERT INTO role_permissions (role_id, permission_code)
SELECT r.id, p.code
FROM roles r,
     permissions p
WHERE r.name = 'Viewer'
  AND p.code IN (
                 'SERVER_VIEW',
                 'MOD_VIEW',
                 'SCENARIO_VIEW',
                 'INSTALL_VIEW',
                 'ADDITIONAL_SERVER_VIEW',
                 'SYSTEM_VIEW'
    );
