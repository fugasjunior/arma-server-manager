package cz.forgottenempire.servermanager.e2e;

import cz.forgottenempire.servermanager.ServerManagerApplication;
import org.springframework.boot.SpringApplication;
import org.testcontainers.containers.MySQLContainer;

import java.util.HashMap;
import java.util.Map;

/**
 * Entry point for running the application with E2E test configuration.
 * Starts a MySQL Testcontainer, wires its JDBC URL into Spring, then boots the main application
 * with the "e2e" profile which activates fake external dependencies.
 */
public class E2EApplication {

    public static void main(String[] args) {
        MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");
        mysql.start();
        Runtime.getRuntime().addShutdownHook(new Thread(mysql::stop));

        SpringApplication app = new SpringApplication(ServerManagerApplication.class);

        Map<String, Object> props = new HashMap<>();
        props.put("spring.datasource.url", mysql.getJdbcUrl());
        props.put("spring.datasource.username", mysql.getUsername());
        props.put("spring.datasource.password", mysql.getPassword());
        app.setDefaultProperties(props);
        app.setAdditionalProfiles("e2e");

        app.run(args);
    }
}
