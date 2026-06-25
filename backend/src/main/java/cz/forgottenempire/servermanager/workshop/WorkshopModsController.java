package cz.forgottenempire.servermanager.workshop;

import cz.forgottenempire.servermanager.api.ModsApi;
import cz.forgottenempire.servermanager.api.model.CreatorDlcDto;
import cz.forgottenempire.servermanager.api.model.ModDto;
import cz.forgottenempire.servermanager.api.model.ModsDto;
import cz.forgottenempire.servermanager.api.model.LoadOnHeadlessClientDto;
import cz.forgottenempire.servermanager.api.model.ServerOnlyDto;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.common.exceptions.NotFoundException;
import cz.forgottenempire.servermanager.security.permission.PermissionCode;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class WorkshopModsController implements ModsApi {

    private final WorkshopModsFacade modsFacade;
    private final ModMapper modMapper;

    @Autowired
    public WorkshopModsController(WorkshopModsFacade modsFacade, ModMapper modMapper) {
        this.modsFacade = modsFacade;
        this.modMapper = modMapper;
    }

    @Override
    @Cacheable("workshopModsResponse")
    @PreAuthorize("hasAuthority('" + PermissionCode.MOD_VIEW + "')")
    public ResponseEntity<ModsDto> getMods(ServerType filter) {
        log.debug("Getting all mods");
        List<CreatorDlcDto> creatorDlcDtos = Collections.emptyList();
        if (filter == null || filter == ServerType.ARMA3) {
            creatorDlcDtos = modMapper.creatorDlcsToCreatorDlcDtos(Arma3CDLC.getAll());
        }
        List<ModDto> workshopModDtos = modMapper.modsToModDtos(modsFacade.getAllMods(filter));
        return ResponseEntity.ok(new ModsDto().workshopMods(workshopModDtos).creatorDlcs(creatorDlcDtos));
    }

    @Override
    @PreAuthorize("hasAuthority('" + PermissionCode.MOD_VIEW + "')")
    public ResponseEntity<ModDto> getMod(Long id) {
        WorkshopMod mod = findMod(id);
        return ResponseEntity.ok(modMapper.modToModDto(mod));
    }

    @Override
    @PreAuthorize("hasAuthority('" + PermissionCode.MOD_MODIFY + "')")
    public ResponseEntity<ModsDto> addMods(List<Long> modIds) {
        log.info("Installing or updating mods: {}", modIds);
        List<WorkshopMod> workshopMods = modsFacade.saveAndInstallMods(modIds);
        List<ModDto> dtos = modMapper.modsToModDtos(workshopMods);
        return ResponseEntity.ok(new ModsDto().workshopMods(dtos).creatorDlcs(Collections.emptyList()));
    }

    @Override
    @PreAuthorize("hasAuthority('" + PermissionCode.MOD_MODIFY + "')")
    public ResponseEntity<ModDto> updateMod(Long id) {
        log.info("Installing mod id {}", id);
        WorkshopMod mod = modsFacade.updateMods(List.of(id)).get(0);
        return ResponseEntity.ok(modMapper.modToModDto(mod));
    }

    @Override
    @PreAuthorize("hasAuthority('" + PermissionCode.MOD_DELETE + "')")
    public ResponseEntity<Void> deleteMods(List<Long> modIds) {
        log.info("Uninstalling mods: {}", modIds);
        modIds.forEach(modsFacade::uninstallMod);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PreAuthorize("hasAuthority('" + PermissionCode.MOD_DELETE + "')")
    public ResponseEntity<Void> deleteMod(Long id) {
        log.info("Uninstalling mod {}", id);
        modsFacade.uninstallMod(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PreAuthorize("hasAuthority('" + PermissionCode.MOD_MODIFY + "')")
    public ResponseEntity<Void> setModServerOnly(Long id, ServerOnlyDto serverOnlyDto) {
        WorkshopMod mod = findMod(id);
        modsFacade.setModServerOnly(mod, serverOnlyDto.getServerOnly());
        return ResponseEntity.ok().build();
    }

    @Override
    @PreAuthorize("hasAuthority('" + PermissionCode.MOD_MODIFY + "')")
    public ResponseEntity<Void> setModLoadOnHeadlessClient(Long id, LoadOnHeadlessClientDto loadOnHeadlessClientDto) {
        WorkshopMod mod = findMod(id);
        modsFacade.setModLoadOnHeadlessClient(mod, loadOnHeadlessClientDto.getLoadOnHeadlessClient());
        return ResponseEntity.ok().build();
    }

    private WorkshopMod findMod(Long id) {
        return modsFacade.getMod(id)
                .orElseThrow(() -> new NotFoundException("Mod ID " + id + " does not exist or is not installed"));
    }
}
