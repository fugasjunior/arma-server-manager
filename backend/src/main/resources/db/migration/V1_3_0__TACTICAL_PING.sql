ALTER TABLE arma3difficulty_settings
    ADD tactical_ping_temp TINYINT;

UPDATE arma3difficulty_settings
SET tactical_ping_temp = CASE tactical_ping
                             WHEN 1 THEN 3
                             ELSE 0
    END;

ALTER TABLE arma3difficulty_settings
    DROP COLUMN tactical_ping;

ALTER TABLE arma3difficulty_settings
    CHANGE COLUMN tactical_ping_temp tactical_ping TINYINT;
