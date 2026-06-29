INSERT INTO permissions (code, description)
VALUES ('APPLICATION_LOGS_VIEW', 'View application logs');

INSERT INTO role_permissions (role_id, permission_code)
SELECT r.id, 'APPLICATION_LOGS_VIEW' FROM roles r WHERE r.name IN ('Admin', 'Operator');
