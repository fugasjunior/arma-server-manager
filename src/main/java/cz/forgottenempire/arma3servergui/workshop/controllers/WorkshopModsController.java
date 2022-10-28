package cz.forgottenempire.arma3servergui.workshop.controllers;

import cz.forgottenempire.arma3servergui.common.exceptions.NotFoundException;
import cz.forgottenempire.arma3servergui.server.ServerType;
import cz.forgottenempire.arma3servergui.workshop.Arma3CDLC;
import cz.forgottenempire.arma3servergui.workshop.dtos.CreatorDlcDto;
import cz.forgottenempire.arma3servergui.workshop.dtos.ModDto;
import cz.forgottenempire.arma3servergui.workshop.dtos.ModsDto;
import cz.forgottenempire.arma3servergui.workshop.entities.WorkshopMod;
import cz.forgottenempire.arma3servergui.workshop.mappers.ModMapper;
import cz.forgottenempire.arma3servergui.workshop.services.WorkshopModsFacade;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/mod")
public class WorkshopModsController {

    private final WorkshopModsFacade modsFacade;
    private final ModMapper modMapper = Mappers.getMapper(ModMapper.class);

    @Autowired
    public WorkshopModsController(WorkshopModsFacade modsFacade) {
        this.modsFacade = modsFacade;
    }

    @GetMapping
    public ResponseEntity<ModsDto> getAllMods(@RequestParam(required = false) ServerType filter) {
        List<CreatorDlcDto> creatorDlcDtos = Collections.emptyList();
        if (filter == null || filter == ServerType.ARMA3) {
             creatorDlcDtos = modMapper.creatorDlcsToCreatorDlcDtos(Arma3CDLC.getAll());
        }
        List<ModDto> workshopModDtos = modMapper.modsToModDtos(modsFacade.getAllMods(filter));
        ModsDto modsDto = new ModsDto(workshopModDtos, creatorDlcDtos);
        return ResponseEntity.ok(modsDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModDto> getMod(@PathVariable Long id) {
        WorkshopMod mod = modsFacade.getMod(id)
                .orElseThrow(() -> new NotFoundException("Mod ID " + id + " does not exist or is not installed"));
        return ResponseEntity.ok(modMapper.modToModDto(mod));
    }

    @PostMapping
    public ResponseEntity<ModsDto> installOrUpdateMods(@RequestParam List<Long> modIds) {
        log.info("Installing or updating mods: {}", modIds);
        List<WorkshopMod> workshopMods = modsFacade.saveAndInstallMods(modIds);
        return ResponseEntity.ok(new ModsDto(modMapper.modsToModDtos(workshopMods), Collections.emptyList()));
    }

    @PostMapping("/{id}")
    public ResponseEntity<ModDto> installOrUpdateMod(@PathVariable Long id) {
        log.info("Installing mod id {}", id);
        WorkshopMod mod = modsFacade.saveAndInstallMods(List.of(id)).get(0);
        return ResponseEntity.ok(modMapper.modToModDto(mod));
    }

    @DeleteMapping
    public ResponseEntity<?> uninstallMods(@RequestParam List<Long> modIds) {
        log.info("Uninstalling mods: {}", modIds);
        modIds.forEach(modsFacade::uninstallMod);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> uninstallMod(@PathVariable Long id) {
        log.info("Uninstalling mod {}", id);
        modsFacade.uninstallMod(id);
        return ResponseEntity.noContent().build();
    }
}
