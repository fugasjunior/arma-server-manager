CREATE TABLE local_mod_bikey
(
    local_mod_id BIGINT       NOT NULL,
    bikey        VARCHAR(255) NOT NULL,
    PRIMARY KEY (local_mod_id, bikey),
    FOREIGN KEY (local_mod_id) REFERENCES local_mod (id) ON DELETE CASCADE
);
