package cz.forgottenempire.arma3servergui.controllers;

import cz.forgottenempire.arma3servergui.model.WorkshopMod;
import cz.forgottenempire.arma3servergui.services.WorkshopModsService;
import java.util.Collection;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/mods")
public class WorkshopModsController {

    private WorkshopModsService modsService;

    @GetMapping
    public ResponseEntity<Collection<WorkshopMod>> getAllMods() {
        return new ResponseEntity<>(modsService.getAllMods(), HttpStatus.OK);
    }

    @PostMapping("/install/{id}")
    public ResponseEntity<WorkshopMod> installOrUpdateMod(@PathVariable Long id) {
        log.info("Installing mod id {}", id);
        WorkshopMod mod = modsService.installOrUpdateMod(id);
        return new ResponseEntity<>(mod, HttpStatus.OK);
    }

    @DeleteMapping("/uninstall/{id}")
    public ResponseEntity<WorkshopMod> uninstallMod(@PathVariable Long id) {
        log.info("Uninstalling mod {}", id);
        modsService.uninstallMod(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/setActive/{id}")
    public ResponseEntity<WorkshopMod> setActive(@PathVariable Long id, @RequestParam boolean active) {
        modsService.activateMod(id, active);
        log.info("Mod {} successfully {}", id, (active ? "activated" : "deactivated"));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/setMultipleActive")
    public ResponseEntity<WorkshopMod> setActive(@RequestBody Map<Long, Boolean> mods) {
        mods.forEach((id, active) -> {
            modsService.activateMod(id, active);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/updateAll")
    public ResponseEntity<WorkshopMod> refreshMods() {
        log.info("Updating all mods");
        modsService.updateAllMods();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Autowired
    public void setModsService(WorkshopModsService modsService) {
        this.modsService = modsService;
    }
}
