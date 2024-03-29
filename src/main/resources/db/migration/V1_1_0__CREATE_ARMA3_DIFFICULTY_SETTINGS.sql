CREATE TABLE arma3difficulty_settings
(
    id                BIGINT AUTO_INCREMENT NOT NULL,
    server_id         BIGINT                NULL,
    group_indicators  TINYINT               NOT NULL,
    friendly_tags     TINYINT               NOT NULL,
    enemy_tags        TINYINT               NOT NULL,
    detected_mines    TINYINT               NOT NULL,
    commands          TINYINT               NOT NULL,
    waypoints         TINYINT               NOT NULL,
    weapon_info       TINYINT               NOT NULL,
    stance_indicator  TINYINT               NOT NULL,
    third_person_view TINYINT               NOT NULL,
    reduced_damage    BIT(1)                NOT NULL,
    tactical_ping     BIT(1)                NOT NULL,
    stamina_bar       BIT(1)                NOT NULL,
    weapon_crosshair  BIT(1)                NOT NULL,
    vision_aid        BIT(1)                NOT NULL,
    score_table       BIT(1)                NOT NULL,
    death_messages    BIT(1)                NOT NULL,
    vonid             BIT(1)                NOT NULL,
    map_content       BIT(1)                NOT NULL,
    auto_report       BIT(1)                NOT NULL,
    camera_shake      BIT(1)                NOT NULL,
    ai_level_preset   TINYINT               NOT NULL,
    skillai           DOUBLE                NOT NULL,
    precisionai       DOUBLE                NOT NULL,
    CONSTRAINT pk_arma3difficultysettings PRIMARY KEY (id)
);

ALTER TABLE arma3difficulty_settings
    ADD CONSTRAINT FK_ARMA3DIFFICULTYSETTINGS_ON_SERVER FOREIGN KEY (server_id) REFERENCES arma3server (id);

INSERT INTO arma3difficulty_settings (server_id, group_indicators, friendly_tags, enemy_tags, detected_mines, commands,
                                      waypoints, weapon_info, stance_indicator, third_person_view, reduced_damage,
                                      tactical_ping, stamina_bar, weapon_crosshair, vision_aid, score_table,
                                      death_messages, vonid, map_content, auto_report, camera_shake, ai_level_preset,
                                      skillai, precisionai)
SELECT id,
       0,
       0,
       0,
       0,
       1,
       1,
       2,
       2,
       0,
       0,
       0,
       0,
       0,
       0,
       1,
       1,
       1,
       0,
       0,
       1,
       3,
       0.5,
       0.5
FROM arma3server;