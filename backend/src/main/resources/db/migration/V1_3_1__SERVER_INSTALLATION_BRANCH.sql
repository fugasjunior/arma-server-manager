CREATE TABLE available_branches
(
    type   VARCHAR(255) NOT NULL,
    branch VARCHAR(255) NOT NULL,
    PRIMARY KEY (type, branch),
    CONSTRAINT fk_available_branches_server_installation FOREIGN KEY (type) REFERENCES server_installation (type)
);

INSERT INTO available_branches (type, branch)
VALUES ('ARMA3', 'CREATORDLC'),
       ('ARMA3', 'PUBLIC'),
       ('ARMA3', 'PROFILING'),
       ('ARMA3', 'CONTACT'),
       ('DAYZ', 'PUBLIC'),
       ('DAYZ_EXP', 'PUBLIC'),
       ('REFORGER', 'PUBLIC');

ALTER TABLE server_installation
    ADD branch VARCHAR(255) NOT NULL DEFAULT 'PUBLIC';

-- Update existing server installations
UPDATE server_installation
SET branch = IF(type = 'ARMA3', 'CREATORDLC', 'PUBLIC');

-- Create default server installation instances if they don't exist yet
INSERT IGNORE INTO server_installation (type, branch)
VALUES ('ARMA3', 'CREATORDLC'),
       ('DAYZ', 'PUBLIC'),
       ('DAYZ_EXP', 'PUBLIC'),
       ('REFORGER', 'PUBLIC');
