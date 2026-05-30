package cz.forgottenempire.servermanager.e2e;

import cz.forgottenempire.servermanager.support.FakeSteamApiConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("e2e")
@Import(FakeSteamApiConfig.class)
class E2EConfig {
}
