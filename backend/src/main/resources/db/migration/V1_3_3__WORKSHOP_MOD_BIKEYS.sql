CREATE TABLE workshop_mod_bikey
(
    workshop_mod_id BIGINT       NOT NULL,
    bikey           VARCHAR(255) NOT NULL,
    PRIMARY KEY (workshop_mod_id, bikey),
    FOREIGN KEY (workshop_mod_id) REFERENCES workshop_mod (id) ON DELETE CASCADE
);