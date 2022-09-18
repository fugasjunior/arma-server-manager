package cz.forgottenempire.arma3servergui.workshop.controllers;

import cz.forgottenempire.arma3servergui.workshop.dtos.ModPresetDto;
import cz.forgottenempire.arma3servergui.workshop.entities.ModListPreset;
import cz.forgottenempire.arma3servergui.workshop.services.services.ModPresetsService;
import java.util.Collection;
import javax.validation.Valid;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/mods/presets")
public class ModPresetsController {

    private ModPresetsService presetsService;

    @GetMapping
    public ResponseEntity<Collection<ModListPreset>> getAllPresets() {
        return new ResponseEntity<>(presetsService.getAllPresets(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createPreset(@RequestBody @Valid ModPresetDto modPresetDto) {
        presetsService.createOrUpdatePreset(modPresetDto.getName(), modPresetDto.getModIds());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("{name}")
    public ResponseEntity<?> deletePreset(@PathVariable String name) {
        presetsService.deletePreset(name);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Autowired
    public void setPresetsService(ModPresetsService presetsService) {
        this.presetsService = presetsService;
    }
}
