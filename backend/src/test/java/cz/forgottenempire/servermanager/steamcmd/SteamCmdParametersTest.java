package cz.forgottenempire.servermanager.steamcmd;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SteamCmdParametersTest {

    @Test
    void continueOnFailedCommandOverridesDefaultShutdownBehavior() {
        SteamCmdParameters parameters = new SteamCmdParameters.Builder()
                .continueOnFailedCommand()
                .withCachedLogin("test")
                .build();

        assertThat(parameters.get())
                .contains("+@ShutdownOnFailedCommand 0")
                .doesNotContain("+@ShutdownOnFailedCommand 1");
    }
}
