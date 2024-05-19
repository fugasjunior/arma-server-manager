package cz.forgottenempire.servermanager.steamcmd;

import cz.forgottenempire.servermanager.steamcmd.outputprocessor.SteamCmdItemInfo;
import cz.forgottenempire.servermanager.steamcmd.outputprocessor.SteamCmdItemInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/steamcmd")
class SteamCmdController {

    private final SteamCmdItemInfoRepository itemInfoRepository;

    @Autowired
    public SteamCmdController(SteamCmdItemInfoRepository itemInfoRepository) {
        this.itemInfoRepository = itemInfoRepository;
    }

    @GetMapping
    public ResponseEntity<Map<Long, SteamCmdItemInfo>> getItemInfos() {
        return ResponseEntity.ok(itemInfoRepository.getAll());
    }
}
