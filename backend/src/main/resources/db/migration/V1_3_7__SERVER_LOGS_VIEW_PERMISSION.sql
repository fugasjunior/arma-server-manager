INSERT INTO permissions (code, description) VALUES
    ('SERVER_LOGS_VIEW', 'View server log files');

-- Assign to Admin (already has all permissions via wildcard insert, but Admin is built-in so add explicitly)
INSERT INTO role_permissions (role_id, permission_code)
SELECT r.id, 'SERVER_LOGS_VIEW'
FROM roles r
WHERE r.name IN ('Admin', 'Operator', 'Viewer');
