package cz.forgottenempire.servermanager.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Clock;

@Configuration
public class CommonConfiguration {
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
