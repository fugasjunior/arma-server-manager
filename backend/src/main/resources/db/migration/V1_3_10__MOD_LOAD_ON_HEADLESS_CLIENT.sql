ALTER TABLE workshop_mod ADD COLUMN load_on_headless_client BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE local_mod    ADD COLUMN load_on_headless_client BOOLEAN NOT NULL DEFAULT TRUE;
