CREATE TABLE app_settings (
                              id                                BIGINT       NOT NULL AUTO_INCREMENT,
                              automatic_mod_update_enabled      BOOLEAN      NOT NULL DEFAULT TRUE,
                              automatic_mod_update_time         TIME         NOT NULL DEFAULT '03:00:00',
                              PRIMARY KEY (id)
);

INSERT INTO app_settings (id, automatic_mod_update_enabled, automatic_mod_update_time)
VALUES (1, TRUE, '03:00:00');

INSERT INTO permissions (code, description)
VALUES ('MANAGE_APP_SETTINGS', 'Manage application settings');

INSERT INTO role_permissions (role_id, permission_code)
SELECT r.id, 'MANAGE_APP_SETTINGS' FROM roles r WHERE r.name IN ('Admin', 'Operator');
