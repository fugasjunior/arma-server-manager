ALTER TABLE arma3server_active_mods ADD COLUMN mod_order INT NULL;
ALTER TABLE dayzserver_active_mods  ADD COLUMN mod_order INT NULL;
ALTER TABLE preset_mod              ADD COLUMN mod_order INT NULL;

UPDATE arma3server_active_mods t
    JOIN (
        SELECT arma3server_id, active_mods_id,
               ROW_NUMBER() OVER (PARTITION BY arma3server_id ORDER BY active_mods_id) - 1 AS rn
        FROM arma3server_active_mods
    ) s ON s.arma3server_id = t.arma3server_id AND s.active_mods_id = t.active_mods_id
SET t.mod_order = s.rn;

UPDATE dayzserver_active_mods t
    JOIN (
        SELECT dayzserver_id, active_mods_id,
               ROW_NUMBER() OVER (PARTITION BY dayzserver_id ORDER BY active_mods_id) - 1 AS rn
        FROM dayzserver_active_mods
    ) s ON s.dayzserver_id = t.dayzserver_id AND s.active_mods_id = t.active_mods_id
SET t.mod_order = s.rn;

UPDATE preset_mod t
    JOIN (
        SELECT preset_id, mod_id,
               ROW_NUMBER() OVER (PARTITION BY preset_id ORDER BY mod_id) - 1 AS rn
        FROM preset_mod
    ) s ON s.preset_id = t.preset_id AND s.mod_id = t.mod_id
SET t.mod_order = s.rn;

ALTER TABLE arma3server_active_mods MODIFY mod_order INT NOT NULL;
ALTER TABLE dayzserver_active_mods  MODIFY mod_order INT NOT NULL;
ALTER TABLE preset_mod              MODIFY mod_order INT NOT NULL;

ALTER TABLE arma3server_activedlcs ADD COLUMN dlc_order INT NULL;

UPDATE arma3server_activedlcs t
    JOIN (
        SELECT arma3server_id, activedlcs,
               ROW_NUMBER() OVER (PARTITION BY arma3server_id ORDER BY activedlcs) - 1 AS rn
        FROM arma3server_activedlcs
    ) s ON s.arma3server_id = t.arma3server_id AND s.activedlcs = t.activedlcs
SET t.dlc_order = s.rn;

ALTER TABLE arma3server_activedlcs MODIFY dlc_order INT NOT NULL;
