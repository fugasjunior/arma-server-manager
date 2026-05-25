CREATE TABLE local_mod
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(255) NOT NULL,
    server_type VARCHAR(32)  NOT NULL,
    file_size   BIGINT,
    server_only BOOLEAN      NOT NULL DEFAULT FALSE,
    uploaded_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_local_mod_type_name (server_type, name),
    INDEX idx_local_mod_server_type (server_type)
);

CREATE TABLE arma3server_active_local_mods
(
    arma3server_id        BIGINT NOT NULL,
    active_local_mods_id  BIGINT NOT NULL,
    mod_order             INT    NOT NULL,
    PRIMARY KEY (arma3server_id, mod_order),
    CONSTRAINT fk_a3_local_mod_server FOREIGN KEY (arma3server_id) REFERENCES arma3server (id) ON DELETE CASCADE,
    CONSTRAINT fk_a3_local_mod        FOREIGN KEY (active_local_mods_id) REFERENCES local_mod (id) ON DELETE CASCADE
);

CREATE TABLE dayzserver_active_local_mods
(
    dayzserver_id        BIGINT NOT NULL,
    active_local_mods_id BIGINT NOT NULL,
    mod_order            INT    NOT NULL,
    PRIMARY KEY (dayzserver_id, mod_order),
    CONSTRAINT fk_dz_local_mod_server FOREIGN KEY (dayzserver_id) REFERENCES dayzserver (id) ON DELETE CASCADE,
    CONSTRAINT fk_dz_local_mod        FOREIGN KEY (active_local_mods_id) REFERENCES local_mod (id) ON DELETE CASCADE
);
