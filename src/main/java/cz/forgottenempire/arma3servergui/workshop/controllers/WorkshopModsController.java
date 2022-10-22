package cz.forgottenempire.arma3servergui.workshop.controllers;

import cz.forgottenempire.arma3servergui.common.exceptions.NotFoundException;
import cz.forgottenempire.arma3servergui.workshop.Arma3CDLC;
import cz.forgottenempire.arma3servergui.workshop.dtos.CreatorDlcDto;
import cz.forgottenempire.arma3servergui.workshop.dtos.ModDto;
import cz.forgottenempire.arma3servergui.workshop.dtos.ModsDto;
import cz.forgottenempire.arma3servergui.workshop.entities.WorkshopMod;
import cz.forgottenempire.arma3servergui.workshop.mappers.ModMapper;
import cz.forgottenempire.arma3servergui.workshop.services.WorkshopModsService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/mod")
public class WorkshopModsController {

    private WorkshopModsService modsService;

    ModMapper modMapper = Mappers.getMapper(ModMapper.class);

    @GetMapping
    public ResponseEntity<ModsDto> getAllMods() {
        List<CreatorDlcDto> creatorDlcDtos = modMapper.creatorDlcsToCreatorDlcDtos(Arma3CDLC.getAll());
        List<ModDto> workshopModDtos = modMapper.modsToModDtos(modsService.getAllMods());
        ModsDto modsDto = new ModsDto(workshopModDtos, creatorDlcDtos);
        return ResponseEntity.ok(modsDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModDto> getMod(@PathVariable Long id) {
        WorkshopMod mod = modsService.getMod(id)
                .orElseThrow(() -> new NotFoundException("Mod ID " + id + " does not exist or is not installed"));
        return ResponseEntity.ok(modMapper.modToModDto(mod));
    }

    @PostMapping("/{id}")
    public ResponseEntity<ModDto> installOrUpdateMod(@PathVariable Long id) {
        log.info("Installing mod id {}", id);
        WorkshopMod mod = modsService.installOrUpdateMod(id);
        return ResponseEntity.ok(modMapper.modToModDto(mod));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> uninstallMod(@PathVariable Long id) {
        log.info("Uninstalling mod {}", id);
        modsService.uninstallMod(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/update")
    public ResponseEntity<WorkshopMod> updateAllMods() {
        log.info("Updating all mods");
        modsService.updateAllMods();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Autowired
    public void setModsService(WorkshopModsService modsService) {
        this.modsService = modsService;
    }
}
