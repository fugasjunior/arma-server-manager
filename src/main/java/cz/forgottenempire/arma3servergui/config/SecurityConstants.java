package cz.forgottenempire.arma3servergui.config;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class SecurityConstants {
    public static String secret;
    public static final long EXPIRATION_TIME = 864_000_000; // 10 days
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";

    @Value("${jwt.secret}")
    public void setSecret(String secret) {
        SecurityConstants.secret = secret;
    }

    public static String getSecret() {
        return secret;
    }
}