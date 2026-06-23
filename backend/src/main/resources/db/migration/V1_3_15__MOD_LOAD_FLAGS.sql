ALTER TABLE workshop_mod ADD COLUMN load_on_client BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE workshop_mod ADD COLUMN load_on_server BOOLEAN NOT NULL DEFAULT TRUE;
UPDATE workshop_mod SET load_on_client = NOT server_only, load_on_server = TRUE;
ALTER TABLE workshop_mod DROP COLUMN server_only;

ALTER TABLE local_mod ADD COLUMN load_on_client BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE local_mod ADD COLUMN load_on_server BOOLEAN NOT NULL DEFAULT TRUE;
UPDATE local_mod SET load_on_client = NOT server_only, load_on_server = TRUE;
ALTER TABLE local_mod DROP COLUMN server_only;
