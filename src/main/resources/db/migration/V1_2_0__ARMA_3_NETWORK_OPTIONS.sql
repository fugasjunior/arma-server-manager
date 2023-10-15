CREATE TABLE arma3_network_settings
(
    id                     BIGINT AUTO_INCREMENT NOT NULL,
    max_msg_send           INT,
    max_size_guaranteed    INT,
    max_size_nonguaranteed INT,
    min_bandwidth          INT,
    max_bandwidth          INT,
    min_error_to_send      DOUBLE,
    min_error_to_send_near DOUBLE,
    max_packet_size        INT,
    max_custom_file_size   INT,
    CONSTRAINT pk_arma3_network_settings PRIMARY KEY (id)
);

ALTER TABLE arma3server
    ADD network_settings_id    BIGINT,
    ADD difficulty_settings_id BIGINT,
    ADD CONSTRAINT fk_arma3_network_settings FOREIGN KEY (network_settings_id)
        REFERENCES arma3_network_settings (id)
        ON DELETE SET NULL,
    ADD CONSTRAINT fk_arma3_difficulty_settings FOREIGN KEY (difficulty_settings_id)
        REFERENCES arma3difficulty_settings (id)
        ON DELETE SET NULL;

UPDATE arma3server s
SET s.difficulty_settings_id = (SELECT id FROM arma3difficulty_settings d WHERE d.server_id = s.id);

ALTER TABLE arma3difficulty_settings
    DROP CONSTRAINT FK_ARMA3DIFFICULTYSETTINGS_ON_SERVER,
    DROP COLUMN server_id;