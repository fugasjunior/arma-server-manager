package cz.forgottenempire.servermanager.steamcmd;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SteamCmdParametersTest {

    @Test
    void continueOnFailedCommandOverridesDefaultShutdownBehavior() {
        SteamCmdParameters parameters = new SteamCmdParameters.Builder()
                .continueOnFailedCommand()
                .withAnonymousLogin()
                .build();

        assertThat(parameters.get())
                .contains("+@ShutdownOnFailedCommand 0")
                .doesNotContain("+@ShutdownOnFailedCommand 1");
    }
}
