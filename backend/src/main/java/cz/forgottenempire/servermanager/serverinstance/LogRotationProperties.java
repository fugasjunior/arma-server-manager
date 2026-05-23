package cz.forgottenempire.servermanager.serverinstance;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "log.rotation")
@Data
public class LogRotationProperties {
    private int maxFiles = 5;
    private long maxSizeMb = 10;
}
