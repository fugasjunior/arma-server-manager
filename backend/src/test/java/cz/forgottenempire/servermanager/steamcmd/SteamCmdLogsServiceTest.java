package cz.forgottenempire.servermanager.steamcmd;

import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.serverinstance.LogFile;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SteamCmdLogsServiceTest {

    @Test
    void whenGettingLogFile_thenLogFileReturned() {
        PathsFactory pathsFactory = mock(PathsFactory.class, withSettings().stubOnly());
        File logFile = mock(File.class, withSettings().stubOnly());
        when(pathsFactory.getSteamCmdLogFile()).thenReturn(logFile);
        SteamCmdLogsService logsService = new SteamCmdLogsService(pathsFactory);

        LogFile log = logsService.getLogFile();

        assertThat(log).isEqualTo(new LogFile(logFile));
    }
}
