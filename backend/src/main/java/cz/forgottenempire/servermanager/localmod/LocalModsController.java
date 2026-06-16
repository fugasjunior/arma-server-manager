package cz.forgottenempire.servermanager.localmod;

import cz.forgottenempire.servermanager.api.LocalModsApi;
import cz.forgottenempire.servermanager.api.model.LocalModDto;
import cz.forgottenempire.servermanager.api.model.LocalModSyncStatusDto;
import cz.forgottenempire.servermanager.api.model.LocalModsDto;
import cz.forgottenempire.servermanager.api.model.ModFlagsDto;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.security.permission.PermissionCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@Slf4j
public class LocalModsController implements LocalModsApi {

    private final LocalModFacade facade;
    private final LocalModService service;
    private final LocalModMapper mapper;

    @Autowired
    public LocalModsController(LocalModFacade facade, LocalModService service, LocalModMapper mapper) {
        this.facade = facade;
        this.service = service;
        this.mapper = mapper;
    }

    @Override
    @PreAuthorize("hasAuthority('" + PermissionCode.MOD_VIEW + "')")
    public ResponseEntity<LocalModsDto> getLocalMods(ServerType filter) {
        List<LocalMod> mods = filter != null
                ? service.getAllMods(filter)
                : service.getAllMods();
        List<LocalModDto> dtos = mapper.toDtos(mods);
        return ResponseEntity.ok(new LocalModsDto().localMods(dtos));
    }


    @Override
    @PreAuthorize("hasAuthority('" + PermissionCode.MOD_MODIFY + "')")
    public ResponseEntity<Void> setLocalModFlags(Long id, ModFlagsDto modFlagsDto) {
        facade.setFlags(id, modFlagsDto);
        return ResponseEntity.ok().build();
    }

    @Override
    @PreAuthorize("hasAuthority('" + PermissionCode.MOD_MODIFY + "')")
    public ResponseEntity<Void> syncLocalMods() {
        facade.startSync();
        return ResponseEntity.accepted().build();
    }

    @Override
    @PreAuthorize("hasAuthority('" + PermissionCode.MOD_VIEW + "')")
    public ResponseEntity<LocalModSyncStatusDto> getLocalModSyncStatus() {
        LocalModSyncStatus status = facade.getSyncStatus();
        LocalModSyncStatusDto.StatusEnum statusEnum = LocalModSyncStatusDto.StatusEnum.valueOf(status.name());
        return ResponseEntity.ok(new LocalModSyncStatusDto().status(statusEnum));
    }
}
