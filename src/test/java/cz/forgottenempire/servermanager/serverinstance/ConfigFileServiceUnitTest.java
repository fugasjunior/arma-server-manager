package cz.forgottenempire.servermanager.serverinstance;

import cz.forgottenempire.servermanager.common.Constants;
import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.serverinstance.entities.Arma3Server;
import cz.forgottenempire.servermanager.serverinstance.entities.ReforgerServer;
import cz.forgottenempire.servermanager.serverinstance.entities.Server;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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

    private void mockFreeMarker() throws IOException {
        freeMarkerConfigurer = mock(FreeMarkerConfigurer.class);
        freeMarkerConfig = mock(Configuration.class);
        template = mock(Template.class);
        when(freeMarkerConfigurer.getConfiguration()).thenReturn(freeMarkerConfig);
        when(freeMarkerConfig.getTemplate(anyString())).thenReturn(template);
    }
}