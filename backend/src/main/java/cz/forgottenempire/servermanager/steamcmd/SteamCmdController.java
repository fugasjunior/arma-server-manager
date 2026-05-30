package cz.forgottenempire.servermanager.steamcmd;

import cz.forgottenempire.servermanager.api.SteamCmdApi;
import cz.forgottenempire.servermanager.api.model.SteamCmdItemInfoDto;
import cz.forgottenempire.servermanager.api.model.SteamCmdStatus;
import cz.forgottenempire.servermanager.common.exceptions.NotFoundException;
import cz.forgottenempire.servermanager.security.permission.PermissionCode;
import cz.forgottenempire.servermanager.serverinstance.LogFile;
import cz.forgottenempire.servermanager.steamcmd.outputprocessor.SteamCmdItemInfo;
import cz.forgottenempire.servermanager.steamcmd.outputprocessor.SteamCmdItemInfoRepository;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasAuthority('" + PermissionCode.STEAM_AUTH_ADMIN + "')")
public class SteamCmdController implements SteamCmdApi {

    private final SteamCmdItemInfoRepository itemInfoRepository;
    private final SteamCmdLogsService logsService;

    @Autowired
    public SteamCmdController(SteamCmdItemInfoRepository itemInfoRepository, SteamCmdLogsService logsService) {
        this.itemInfoRepository = itemInfoRepository;
        this.logsService = logsService;
    }

    @Override
    public ResponseEntity<Map<String, SteamCmdItemInfoDto>> getSteamCmdItemInfos() {
        Map<String, SteamCmdItemInfoDto> result = itemInfoRepository.getAll().entrySet().stream()
                .collect(Collectors.toMap(
                        e -> String.valueOf(e.getKey()),
                        e -> toDto(e.getValue())
                ));
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<String> getSteamCmdLog(Integer count) {
        LogFile logFile = logsService.getLogFile();
        return ResponseEntity.ok(logFile.getLastLines(count));
    }

    @Override
    public ResponseEntity<Resource> downloadSteamCmdLog() {
        try {
            Resource resource = logsService.getLogFile().asResource()
                    .orElseThrow(() -> new NotFoundException("SteamCMD log file doesn't exist"));

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFile().getName());
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(resource.contentLength())
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(resource);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read SteamCMD log file", e);
        }
    }

    private SteamCmdItemInfoDto toDto(SteamCmdItemInfo info) {
        return new SteamCmdItemInfoDto()
                .itemId(info.itemId())
                .status(SteamCmdStatus.fromValue(info.status().name()))
                .progressPercent(info.progressPercent())
                .bytesFinished(info.bytesFinished())
                .bytesTotal(info.bytesTotal());
    }
}
