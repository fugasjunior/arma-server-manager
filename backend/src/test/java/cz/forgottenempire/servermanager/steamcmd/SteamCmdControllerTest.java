package cz.forgottenempire.servermanager.steamcmd;

import cz.forgottenempire.servermanager.common.exceptions.NotFoundException;
import cz.forgottenempire.servermanager.serverinstance.LogFile;
import cz.forgottenempire.servermanager.steamcmd.outputprocessor.SteamCmdItemInfo;
import cz.forgottenempire.servermanager.steamcmd.outputprocessor.SteamCmdItemInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SteamCmdControllerTest {
    @Mock(stubOnly = true)
    private SteamCmdLogsService logsService;
    @Mock(stubOnly = true)
    private SteamCmdItemInfoRepository itemInfoRepository;

    private SteamCmdController steamCmdController;

    @BeforeEach
    void setUp() {
        steamCmdController = new SteamCmdController(itemInfoRepository, logsService);
    }

    @Test
    void getItemInfos() {
        SteamCmdItemInfo itemInfo = new SteamCmdItemInfo(1L, SteamCmdItemInfo.SteamCmdStatus.FINISHED, 100, 1000L, 1000L);
        Map<Long, SteamCmdItemInfo> expectedItemInfos = Map.of(1L, itemInfo);
        when(itemInfoRepository.getAll()).thenReturn(expectedItemInfos);

        ResponseEntity<Map<Long, SteamCmdItemInfo>> itemInfos = steamCmdController.getItemInfos();

        assertThat(itemInfos).isEqualTo(ResponseEntity.ok(expectedItemInfos));
    }

    @Test
    void downloadExistingLogFile() throws IOException {
        LogFile logFile = mock(LogFile.class, withSettings().stubOnly());
        when(logsService.getLogFile()).thenReturn(logFile);
        Resource resource = mock(Resource.class, withSettings().stubOnly());
        when(logFile.asResource()).thenReturn(Optional.of(resource));
        File file = mock(File.class, withSettings().stubOnly());
        when(resource.getFile()).thenReturn(file);
        when(file.getName()).thenReturn("log_file.log");

        ResponseEntity<Resource> response = steamCmdController.downloadLogFile();

        HttpHeaders expectedHeaders = new HttpHeaders();
        expectedHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=log_file.log");
        ResponseEntity<Resource> expectedResponse = ResponseEntity.ok()
                .headers(expectedHeaders)
                .contentLength(resource.contentLength())
                .contentType(MediaType.TEXT_PLAIN)
                .body(resource);
        assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    void downloadNonExistentLogFileThrowsNotFoundException() throws IOException {
        LogFile logFile = mock(LogFile.class, withSettings().stubOnly());
        when(logsService.getLogFile()).thenReturn(logFile);
        when(logFile.asResource()).thenReturn(Optional.empty());

        assertThatThrownBy(() -> steamCmdController.downloadLogFile())
                .isInstanceOf(NotFoundException.class)
                .hasMessage("SteamCMD log file doesn't exist");
    }

    @Test
    void getLastFilesFromLog() {
        LogFile logFile = mock(LogFile.class, withSettings().stubOnly());
        String expectedLogLines = """
                Line 1
                Line 2
                Line 3
                """;
        when(logFile.getLastLines(3)).thenReturn(expectedLogLines);
        when(logsService.getLogFile()).thenReturn(logFile);

        ResponseEntity<String> response = steamCmdController.getLastLinesFromLog(3);

        assertThat(response).isEqualTo(ResponseEntity.ok(expectedLogLines));
    }
}
