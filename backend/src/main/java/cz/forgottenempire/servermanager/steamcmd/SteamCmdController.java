package cz.forgottenempire.servermanager.steamcmd;

import cz.forgottenempire.servermanager.common.exceptions.NotFoundException;
import cz.forgottenempire.servermanager.serverinstance.LogFile;
import cz.forgottenempire.servermanager.steamcmd.outputprocessor.SteamCmdItemInfo;
import cz.forgottenempire.servermanager.steamcmd.outputprocessor.SteamCmdItemInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/steamcmd")
class SteamCmdController {

    private static final String DEFAULT_LOG_LINES_COUNT = "100";
    private final SteamCmdItemInfoRepository itemInfoRepository;
    private final SteamCmdLogsService logsService;

    @Autowired
    public SteamCmdController(SteamCmdItemInfoRepository itemInfoRepository, SteamCmdLogsService logsService) {
        this.itemInfoRepository = itemInfoRepository;
        this.logsService = logsService;
    }

    @GetMapping
    public ResponseEntity<Map<Long, SteamCmdItemInfo>> getItemInfos() {
        return ResponseEntity.ok(itemInfoRepository.getAll());
    }

    @GetMapping("/log/download")
    public ResponseEntity<Resource> downloadLogFile() throws IOException {
        Resource resource = logsService.getLogFile().asResource()
                .orElseThrow(() -> new NotFoundException("SteamCMD log file doesn't exist"));

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFile().getName());
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.TEXT_PLAIN)
                .body(resource);
    }

    @GetMapping("/log")
    public ResponseEntity<String> getLastLinesFromLog(@RequestParam(required = false, defaultValue = DEFAULT_LOG_LINES_COUNT) int count) {
        LogFile logFile = logsService.getLogFile();
        String logLines = logFile.getLastLines(count);
        return ResponseEntity.ok(logLines);
    }
}
