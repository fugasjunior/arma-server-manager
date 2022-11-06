package cz.forgottenempire.servermanager.serverinstance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.gson.JsonObject;
import cz.forgottenempire.servermanager.common.Constants;
import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.serverinstance.entities.Arma3Server;
import cz.forgottenempire.servermanager.serverinstance.entities.ReforgerServer;
import cz.forgottenempire.servermanager.serverinstance.entities.Server;
import freemarker.template.Configuration;
import freemarker.template.Template;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

class ConfigFileServiceUnitTest {

    private final PathsFactory pathsFactory;
    private final ConfigFileService configFileService;
    private FreeMarkerConfigurer freeMarkerConfigurer;
    private Configuration freeMarkerConfig;
    private Template template;
    @TempDir
    private Path tempDir;

    public ConfigFileServiceUnitTest() throws Exception {
        mockFreeMarker();
        pathsFactory = mock(PathsFactory.class);

        configFileService = new ConfigFileService(freeMarkerConfigurer, pathsFactory);
    }

    private static Server createServer(ServerType type) {
        Server server;
        if (type == ServerType.REFORGER) {
            server = new ReforgerServer();
        } else {
            server = new Arma3Server();
        }

        server.setType(type);
        server.setId(1L);
        return server;
    }

    private static void writeTestConfigFile(File configFile, String key, String value) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))) {
            JsonObject jsonConfig = new JsonObject();
            jsonConfig.addProperty(key, value);
            writer.write(jsonConfig.toString());
        }
    }

    @Test
    void whenGetConfigForArma3Server_thenReturnCorrectlyNamedCfgFile() {
        Server server = createServer(ServerType.ARMA3);
        when(pathsFactory.getConfigFilePath(ServerType.ARMA3, "ARMA3_1.cfg"))
                .thenReturn(Path.of("servers", "ARMA3", "ARMA3_1.cfg"));

        File configFile = configFileService.getConfigFileForServer(server);

        verify(pathsFactory).getConfigFilePath(ServerType.ARMA3, "ARMA3_1.cfg");
        assertThat(configFile).hasName("ARMA3_1.cfg");
    }

    @Test
    void whenGetConfigForReforgerServer_thenReturnCorrectlyNamedJsonFile() {
        Server server = createServer(ServerType.REFORGER);
        when(pathsFactory.getConfigFilePath(ServerType.REFORGER, "REFORGER_1.json"))
                .thenReturn(Path.of("servers", "ARMA3", "REFORGER_1.json"));

        File configFile = configFileService.getConfigFileForServer(server);

        verify(pathsFactory).getConfigFilePath(ServerType.REFORGER, "REFORGER_1.json");
        assertThat(configFile).hasName("REFORGER_1.json");
    }

    @Test
    public void whenWriteConfig_thenDeleteOldConfigAndCallFreeMarkerWithProperParameters() throws Exception {
        Server server = createServer(ServerType.ARMA3);
        when(pathsFactory.getConfigFilePath(ServerType.ARMA3, "ARMA3_1.cfg"))
                .thenReturn(tempDir.toAbsolutePath());
        File oldConfigFile = tempDir.resolve("ARMA3_1.cfg").toFile();
        oldConfigFile.createNewFile();

        configFileService.writeConfig(server);

        assertThat(oldConfigFile.exists()).isFalse();
        verify(freeMarkerConfigurer).getConfiguration();
        verify(freeMarkerConfig).getTemplate(Constants.SERVER_CONFIG_TEMPLATES.get(ServerType.ARMA3));
        verify(template).process(any(Arma3Server.class), any(Writer.class));
    }

    @Test
    public void whenReadOptionFromConfigAndOptionExists_thenReturnOptionValue() throws IOException {
        Server server = createServer(ServerType.REFORGER);
        File configFile = tempDir.resolve("REFORGER_1.cfg").toFile();
        writeTestConfigFile(configFile, "dedicatedServerId", "ID_12345");
        when(pathsFactory.getConfigFilePath(ServerType.REFORGER, "REFORGER_1.json"))
                .thenReturn(configFile.toPath().toAbsolutePath());

        Optional<String> value = configFileService.readOptionFromConfig("dedicatedServerId", server);

        assertThat(value).isPresent();
        assertThat(value.get()).isEqualTo("ID_12345");
    }

    @Test
    public void whenReadOptionFromConfigAndOptionDoesNotExist_thenReturnEmptyOptional() throws IOException {
        Server server = createServer(ServerType.REFORGER);
        File configFile = tempDir.resolve("REFORGER_1.cfg").toFile();
        writeTestConfigFile(configFile, "key", "value");
        when(pathsFactory.getConfigFilePath(ServerType.REFORGER, "REFORGER_1.json"))
                .thenReturn(configFile.toPath().toAbsolutePath());

        Optional<String> value = configFileService.readOptionFromConfig("NOT_EXISTING_KEY", server);

        assertThat(value).isEmpty();
    }

    @Test
    public void whenReadOptionFromConfigAndNotReforger_thenThrowUnsupportedOperationException() {
        Server server = createServer(ServerType.ARMA3);

        assertThatThrownBy(() -> configFileService.readOptionFromConfig("test", server))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("Reading properties is only implemented for Reforger servers");
    }

    @Test
    public void whenReadOptionFromConfigAndConfigDoesNotExist_thenThrowIllegalStateException() {
        Server server = createServer(ServerType.REFORGER);
        when(pathsFactory.getConfigFilePath(ServerType.REFORGER, "REFORGER_1.json"))
                .thenReturn(tempDir.resolve("REFORGER_1.json").toAbsolutePath());

        assertThatThrownBy(() -> configFileService.readOptionFromConfig("test", server))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(
                        "Cannot read property from config file REFORGER_1.json because it does not exist");
    }

    private void mockFreeMarker() throws IOException {
        freeMarkerConfigurer = mock(FreeMarkerConfigurer.class);
        freeMarkerConfig = mock(Configuration.class);
        template = mock(Template.class);
        when(freeMarkerConfigurer.getConfiguration()).thenReturn(freeMarkerConfig);
        when(freeMarkerConfig.getTemplate(anyString())).thenReturn(template);
    }
}