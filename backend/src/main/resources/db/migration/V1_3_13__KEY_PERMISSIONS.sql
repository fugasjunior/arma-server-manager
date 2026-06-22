-- Add custom bikey management permissions
INSERT INTO permissions (code, description)
VALUES ('BIKEY_VIEW', 'View bikeys'),
       ('BIKEY_MODIFY', 'Upload bikeys'),
       ('BIKEY_DELETE', 'Delete bikeys');

INSERT INTO role_permissions (role_id, permission_code)
SELECT r.id, p.code
FROM roles r,
     permissions p
WHERE r.name = 'Admin'
  AND p.code IN ('BIKEY_VIEW', 'BIKEY_MODIFY', 'BIKEY_DELETE');

INSERT INTO role_permissions (role_id, permission_code)
SELECT r.id, p.code
FROM roles r,
     permissions p
WHERE r.name = 'Operator'
  AND p.code IN ('BIKEY_VIEW');

INSERT INTO role_permissions (role_id, permission_code)
SELECT r.id, p.code
FROM roles r,
     permissions p
WHERE r.name = 'Viewer'
  AND p.code IN ('BIKEY_VIEW');
