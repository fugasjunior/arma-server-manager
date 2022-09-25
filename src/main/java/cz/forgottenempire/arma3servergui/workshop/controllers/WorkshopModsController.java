package cz.forgottenempire.arma3servergui.workshop.controllers;

import cz.forgottenempire.arma3servergui.workshop.dtos.WorkshopModDto;
import cz.forgottenempire.arma3servergui.workshop.dtos.WorkshopModsDto;
import cz.forgottenempire.arma3servergui.workshop.entities.WorkshopMod;
import cz.forgottenempire.arma3servergui.workshop.mappers.WorkshopModMapper;
import cz.forgottenempire.arma3servergui.workshop.services.WorkshopModsService;
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
import org.springframework.web.server.ResponseStatusException;

@RestController
@Slf4j
@RequestMapping("/api/mod")
public class WorkshopModsController {

    private WorkshopModsService modsService;

    WorkshopModMapper workshopModMapper = Mappers.getMapper(WorkshopModMapper.class);

    @GetMapping
    public ResponseEntity<WorkshopModsDto> getAllMods() {
        WorkshopModsDto modsDto = new WorkshopModsDto(workshopModMapper.modsToModDtos(modsService.getAllMods()));
        return ResponseEntity.ok(modsDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkshopModDto> getMod(@PathVariable Long id) {
        WorkshopMod mod = modsService.getMod(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Mod ID " + id + " does not exist or is not installed"));
        return ResponseEntity.ok(workshopModMapper.modToModDto(mod));
    }

    @PostMapping("/{id}")
    public ResponseEntity<WorkshopModDto> installOrUpdateMod(@PathVariable Long id) {
        log.info("Installing mod id {}", id);
        WorkshopMod mod = modsService.installOrUpdateMod(id);
        return ResponseEntity.ok(workshopModMapper.modToModDto(mod));
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
