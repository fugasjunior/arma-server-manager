CREATE TABLE additional_server
(
    id         BIGINT       NOT NULL,
    name       VARCHAR(255) NULL,
    server_dir VARCHAR(255) NULL,
    command    VARCHAR(255) NULL,
    image_url  VARCHAR(255) NULL,
    CONSTRAINT pk_additionalserver PRIMARY KEY (id)
);

CREATE TABLE arma3server
(
    id                   BIGINT   NOT NULL,
    client_file_patching BIT(1)   NOT NULL,
    server_file_patching BIT(1)   NOT NULL,
    persistent           BIT(1)   NOT NULL,
    battl_eye            BIT(1)   NOT NULL,
    von_enabled          BIT(1)   NOT NULL,
    verify_signatures    BIT(1)   NOT NULL,
    additional_options   LONGTEXT NULL,
    CONSTRAINT pk_arma3server PRIMARY KEY (id)
);

CREATE TABLE arma3server_active_mods
(
    arma3server_id BIGINT NOT NULL,
    active_mods_id BIGINT NOT NULL
);

CREATE TABLE arma3server_activedlcs
(
    arma3server_id BIGINT       NOT NULL,
    activedlcs     VARCHAR(255) NULL
);

CREATE TABLE dayzserver
(
    id                        BIGINT   NOT NULL,
    instance_id               BIGINT   NOT NULL,
    respawn_time              INT      NOT NULL,
    persistent                BIT(1)   NOT NULL,
    von_enabled               BIT(1)   NOT NULL,
    force_same_build          BIT(1)   NOT NULL,
    third_person_view_enabled BIT(1)   NOT NULL,
    crosshair_enabled         BIT(1)   NOT NULL,
    client_file_patching      BIT(1)   NOT NULL,
    time_acceleration         DOUBLE   NOT NULL,
    night_time_acceleration   DOUBLE   NOT NULL,
    additional_options        LONGTEXT NULL,
    CONSTRAINT pk_dayzserver PRIMARY KEY (id)
);

CREATE TABLE dayzserver_active_mods
(
    dayzserver_id  BIGINT NOT NULL,
    active_mods_id BIGINT NOT NULL
);

CREATE TABLE mod_preset
(
    id   BIGINT AUTO_INCREMENT NOT NULL,
    name VARCHAR(255)          NULL,
    type VARCHAR(255)          NOT NULL,
    CONSTRAINT pk_modpreset PRIMARY KEY (id)
);

CREATE TABLE preset_mod
(
    mod_id    BIGINT NOT NULL,
    preset_id BIGINT NOT NULL
);

CREATE TABLE reforger_server
(
    id                        BIGINT       NOT NULL,
    dedicated_server_id       VARCHAR(255) NULL,
    scenario_id               VARCHAR(255) NULL,
    third_person_view_enabled BIT(1)       NOT NULL,
    battl_eye                 BIT(1)       NOT NULL,
    CONSTRAINT pk_reforgerserver PRIMARY KEY (id)
);

CREATE TABLE server
(
    id             BIGINT AUTO_INCREMENT NOT NULL,
    type           INT                   NOT NULL,
    `description`  VARCHAR(255)          NULL,
    name           VARCHAR(255)          NULL,
    port           INT                   NOT NULL,
    query_port     INT                   NOT NULL,
    max_players    INT                   NOT NULL,
    password       VARCHAR(255)          NULL,
    admin_password VARCHAR(255)          NULL,
    CONSTRAINT pk_server PRIMARY KEY (id)
);

CREATE TABLE server_installation
(
    type                VARCHAR(255) NOT NULL,
    version             VARCHAR(255) NULL,
    last_updated_at     datetime     NULL,
    installation_status VARCHAR(255) NULL,
    error_status        VARCHAR(255) NULL,
    CONSTRAINT pk_serverinstallation PRIMARY KEY (type)
);

CREATE TABLE steam_auth
(
    id                BIGINT AUTO_INCREMENT NOT NULL,
    username          VARCHAR(255)          NULL,
    password          VARCHAR(255)          NULL,
    steam_guard_token VARCHAR(255)          NULL,
    CONSTRAINT pk_steamauth PRIMARY KEY (id)
);

CREATE TABLE workshop_mod
(
    id                  BIGINT       NOT NULL,
    name                VARCHAR(255) NULL,
    last_updated        VARCHAR(255) NULL,
    file_size           BIGINT       NULL,
    installation_status VARCHAR(255) NULL,
    error_status        VARCHAR(255) NULL,
    server_type         VARCHAR(255) NULL,
    CONSTRAINT pk_workshopmod PRIMARY KEY (id)
);

ALTER TABLE mod_preset
    ADD CONSTRAINT uc_modpreset_name UNIQUE (name);

ALTER TABLE arma3server
    ADD CONSTRAINT FK_ARMA3SERVER_ON_ID FOREIGN KEY (id) REFERENCES server (id);

ALTER TABLE dayzserver
    ADD CONSTRAINT FK_DAYZSERVER_ON_ID FOREIGN KEY (id) REFERENCES server (id);

ALTER TABLE reforger_server
    ADD CONSTRAINT FK_REFORGERSERVER_ON_ID FOREIGN KEY (id) REFERENCES server (id);

ALTER TABLE arma3server_activedlcs
    ADD CONSTRAINT fk_arma3server_activedlcs_on_arma3_server FOREIGN KEY (arma3server_id) REFERENCES arma3server (id);

ALTER TABLE arma3server_active_mods
    ADD CONSTRAINT fk_armactmod_on_arma3_server FOREIGN KEY (arma3server_id) REFERENCES arma3server (id);

ALTER TABLE arma3server_active_mods
    ADD CONSTRAINT fk_armactmod_on_workshop_mod FOREIGN KEY (active_mods_id) REFERENCES workshop_mod (id);

ALTER TABLE dayzserver_active_mods
    ADD CONSTRAINT fk_dayactmod_on_day_z_server FOREIGN KEY (dayzserver_id) REFERENCES dayzserver (id);

ALTER TABLE dayzserver_active_mods
    ADD CONSTRAINT fk_dayactmod_on_workshop_mod FOREIGN KEY (active_mods_id) REFERENCES workshop_mod (id);

ALTER TABLE preset_mod
    ADD CONSTRAINT fk_premod_on_mod_preset FOREIGN KEY (preset_id) REFERENCES mod_preset (id);

ALTER TABLE preset_mod
    ADD CONSTRAINT fk_premod_on_workshop_mod FOREIGN KEY (mod_id) REFERENCES workshop_mod (id);