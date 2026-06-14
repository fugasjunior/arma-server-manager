CREATE TABLE server_config_override (
    id        BIGINT       NOT NULL AUTO_INCREMENT,
    server_id BIGINT       NOT NULL,
    config_key VARCHAR(64) NOT NULL,
    content   LONGTEXT,
    PRIMARY KEY (id),
    UNIQUE KEY uq_override_server_key (server_id, config_key),
    CONSTRAINT fk_override_server FOREIGN KEY (server_id) REFERENCES server (id) ON DELETE CASCADE
);

INSERT INTO permissions (code, description)
VALUES ('ADVANCED_CONFIG_EDIT', 'Edit raw server configuration files in advanced mode. Grants access to server secrets in raw configs. Requires SERVER_SECRETS_VIEW permission.');

INSERT INTO role_permissions (role_id, permission_code)
SELECT r.id, 'ADVANCED_CONFIG_EDIT' FROM roles r WHERE r.name = 'Admin';
