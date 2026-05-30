-- arma3server_active_mods has no PK yet — just add id column and position column
ALTER TABLE arma3server_active_mods
    ADD COLUMN id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY FIRST,
    ADD COLUMN position INT NOT NULL DEFAULT 0;

-- arma3server_active_local_mods has PK (arma3server_id, mod_order) — replace it
ALTER TABLE arma3server_active_local_mods
    DROP PRIMARY KEY,
    ADD COLUMN id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY FIRST,
    ADD UNIQUE KEY uk_a3_active_local_mod (arma3server_id, mod_order),
    ADD COLUMN position INT NOT NULL DEFAULT 0;

-- dayzserver_active_mods has no PK yet
ALTER TABLE dayzserver_active_mods
    ADD COLUMN id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY FIRST,
    ADD COLUMN position INT NOT NULL DEFAULT 0;

-- dayzserver_active_local_mods has PK (dayzserver_id, mod_order) — replace it
ALTER TABLE dayzserver_active_local_mods
    DROP PRIMARY KEY,
    ADD COLUMN id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY FIRST,
    ADD UNIQUE KEY uk_dz_active_local_mod (dayzserver_id, mod_order),
    ADD COLUMN position INT NOT NULL DEFAULT 0;

-- Populate position from existing mod_order (workshop mods first, local appended after)
UPDATE arma3server_active_mods SET position = mod_order;

UPDATE arma3server_active_local_mods l
    JOIN (SELECT arma3server_id, COUNT(*) AS cnt FROM arma3server_active_mods GROUP BY arma3server_id) w
        ON l.arma3server_id = w.arma3server_id
SET l.position = w.cnt + l.mod_order;

UPDATE dayzserver_active_mods SET position = mod_order;

UPDATE dayzserver_active_local_mods l
    JOIN (SELECT dayzserver_id, COUNT(*) AS cnt FROM dayzserver_active_mods GROUP BY dayzserver_id) w
        ON l.dayzserver_id = w.dayzserver_id
SET l.position = w.cnt + l.mod_order;
