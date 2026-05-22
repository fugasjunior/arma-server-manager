package cz.forgottenempire.servermanager.security;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

public class SecretsEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final Logger log = LoggerFactory.getLogger(SecretsEnvironmentPostProcessor.class);

    private static final String SECRETS_FILE = "config/secrets.properties";
    private static final String JWT_SECRET_KEY = "jwt.secret";
    private static final String DB_ENCRYPTION_KEY = "database.encryption.secret";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Properties fileSecrets = loadSecretsFile();
        Properties toGenerate = new Properties();

        if (!environment.containsProperty(JWT_SECRET_KEY) && !fileSecrets.containsKey(JWT_SECRET_KEY)) {
            toGenerate.setProperty(JWT_SECRET_KEY, generateBase64Secret(48));
            log.info("Generated JWT secret");
        }

        if (!environment.containsProperty(DB_ENCRYPTION_KEY) && !fileSecrets.containsKey(DB_ENCRYPTION_KEY)) {
            // AES-256: key must be exactly 32 bytes; Base64 of 24 random bytes = 32 ASCII chars
            toGenerate.setProperty(DB_ENCRYPTION_KEY, generateBase64Secret(24));
            log.info("Generated database encryption secret");
        }

        if (!toGenerate.isEmpty()) {
            fileSecrets.putAll(toGenerate);
            writeSecretsFile(fileSecrets);
            log.info("Persisted generated secrets to {}", SECRETS_FILE);
        }

        Properties toExpose = new Properties();
        toExpose.putAll(fileSecrets);
        toExpose.putAll(toGenerate);

        if (!toExpose.isEmpty()) {
            // addLast = lowest priority, so env vars and application.properties override file secrets
            environment.getPropertySources().addLast(
                    new PropertiesPropertySource("generated-secrets", toExpose));
        }
    }

    private Properties loadSecretsFile() {
        Properties props = new Properties();
        File file = new File(SECRETS_FILE);
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                props.load(reader);
            } catch (IOException e) {
                log.warn("Could not read secrets file {}: {}", SECRETS_FILE, e.getMessage());
            }
        }
        return props;
    }

    private void writeSecretsFile(Properties secrets) {
        File file = new File(SECRETS_FILE);
        try {
            file.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(file)) {
                secrets.store(writer, "Auto-generated secrets — do not commit this file");
            }
        } catch (IOException | SecurityException e) {
            log.error("Could not write secrets file {}: {}", SECRETS_FILE, e.getMessage());
        }
    }

    private String generateBase64Secret(int byteLength) {
        byte[] bytes = new byte[byteLength];
        new SecureRandom().nextBytes(bytes);
        return Base64.getEncoder().withoutPadding().encodeToString(bytes);
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
