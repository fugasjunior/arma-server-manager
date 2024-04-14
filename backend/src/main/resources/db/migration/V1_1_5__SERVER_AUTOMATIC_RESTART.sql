ALTER TABLE server
    ADD automatic_restart      BOOLEAN NOT NULL DEFAULT FALSE,
    ADD automatic_restart_time TIME
