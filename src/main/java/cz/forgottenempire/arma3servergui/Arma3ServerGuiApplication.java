package cz.forgottenempire.arma3servergui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class Arma3ServerGuiApplication {

    public static void main(String[] args) {
        SpringApplication.run(Arma3ServerGuiApplication.class, args);
    }
}
