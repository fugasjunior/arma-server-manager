package cz.forgottenempire.servermanager.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class PathsFactoryTest {

    private Arma3InstancePaths arma3InstancePaths;

    @BeforeEach
    void setUp() {
        PathsFactory pathsFactory = new PathsFactory(
                "/servers",
                "/mods",
                "/logs",
                "/steamcmd/steamcmd",
                "/steamcmd/cache.json"
        );
        arma3InstancePaths = new Arma3InstancePaths(pathsFactory);
    }

    @Test
    void getInstanceBasePath_returnsCorrectPath() {
        Path result = arma3InstancePaths.getInstanceBasePath(42);
        assertThat(result).endsWithRaw(Path.of("servers/ARMA3/profiles/ARMA3_42"));
    }

    @Test
    void getInstanceProfilesPath_equalsBasePath() {
        assertThat(arma3InstancePaths.getInstanceProfilesPath(42))
                .isEqualTo(arma3InstancePaths.getInstanceBasePath(42));
    }

    @Test
    void getInstanceConfigsPath_returnsCorrectPath() {
        Path result = arma3InstancePaths.getInstanceConfigsPath(42);
        assertThat(result).endsWithRaw(Path.of("servers/ARMA3/profiles/ARMA3_42/configs"));
    }

    @Test
    void getInstanceKeysPath_returnsCorrectPath() {
        Path result = arma3InstancePaths.getInstanceKeysPath(42);
        assertThat(result).endsWithRaw(Path.of("servers/ARMA3/profiles/ARMA3_42/keys"));
    }

    @Test
    void getInstanceMpmissionsPath_returnsCorrectPath() {
        Path result = arma3InstancePaths.getInstanceMpmissionsPath(42);
        assertThat(result).endsWithRaw(Path.of("servers/ARMA3/profiles/ARMA3_42/mpmissions"));
    }

    @Test
    void getHeadlessClientProfilesPath_returnsCorrectPath() {
        assertThat(arma3InstancePaths.getHeadlessClientProfilesPath(42, 1))
                .endsWithRaw(Path.of("servers/ARMA3/profiles/ARMA3_42/hc/hc_01"));
        assertThat(arma3InstancePaths.getHeadlessClientProfilesPath(42, 2))
                .endsWithRaw(Path.of("servers/ARMA3/profiles/ARMA3_42/hc/hc_02"));
    }

    @Test
    void instancePaths_useDistinctDirsPerInstance() {
        assertThat(arma3InstancePaths.getInstanceBasePath(1))
                .isNotEqualTo(arma3InstancePaths.getInstanceBasePath(2));
        assertThat(arma3InstancePaths.getInstanceKeysPath(1))
                .isNotEqualTo(arma3InstancePaths.getInstanceKeysPath(99));
    }
}
