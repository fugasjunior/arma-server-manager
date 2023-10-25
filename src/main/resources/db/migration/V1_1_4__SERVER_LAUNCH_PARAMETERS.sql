CREATE TABLE launch_parameter
(
    id        BIGINT AUTO_INCREMENT NOT NULL,
    server_id BIGINT                NOT NULL,
    name      VARCHAR(64)           NOT NULL,
    value     VARCHAR(255),
    CONSTRAINT pk_launch_parameter PRIMARY KEY (id),
    CONSTRAINT fk_launch_parameter_server FOREIGN KEY (server_id)
        REFERENCES server (id),
    INDEX (server_id)
)