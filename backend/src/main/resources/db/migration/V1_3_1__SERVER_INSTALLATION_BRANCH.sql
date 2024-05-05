-- Add new columns
ALTER TABLE server_installation
    ADD active_branch      VARCHAR(255) NOT NULL DEFAULT '',
    ADD available_branches VARCHAR(255) NOT NULL DEFAULT '';

-- Update existing server installations
UPDATE server_installation
SET active_branch      = IF(type = 'ARMA3', 'CREATORDLC', 'PUBLIC'),
    available_branches = IF(type = 'ARMA3', 'PUBLIC,PROFILING,CONTACT,CREATORDLC', 'PUBLIC');

-- Create default server installation instances if they don't exist yet
INSERT IGNORE INTO server_installation (type, active_branch, available_branches)
VALUES ('ARMA3', 'CREATORDLC', 'PUBLIC,PROFILING,CONTACT,CREATORDLC'),
       ('DAYZ', 'PUBLIC', 'PUBLIC'),
       ('DAYZ_EXP', 'PUBLIC', 'PUBLIC'),
       ('REFORGER', 'PUBLIC', 'PUBLIC');
