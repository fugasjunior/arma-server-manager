package cz.forgottenempire.arma3servergui.controllers;

import cz.forgottenempire.arma3servergui.Constants;
import cz.forgottenempire.arma3servergui.model.SteamAuth;
import cz.forgottenempire.arma3servergui.model.WorkshopMod;
import cz.forgottenempire.arma3servergui.services.JsonDbService;
import cz.forgottenempire.arma3servergui.services.WorkshopFileDetailsService;
import cz.forgottenempire.arma3servergui.services.WorkshopInstallerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@Slf4j
@RequestMapping("/api/mods")
public class WorkshopModsController {
    private WorkshopFileDetailsService workshopFileDetailsService;
    private WorkshopInstallerService workshopInstallerService;
    private JsonDbService<SteamAuth> steamAuthDb;
    private JsonDbService<WorkshopMod> modsDb;

    @GetMapping
    public ResponseEntity<Collection<WorkshopMod>> getAllMods() {
        return new ResponseEntity<>(modsDb.findAll(WorkshopMod.class), HttpStatus.OK);
    }

    @PostMapping("/install/{id}")
    public ResponseEntity<WorkshopMod> installOrUpdateMod(@PathVariable Long id) {
        log.info("Installing mod id {}", id);

        WorkshopMod mod = modsDb.find(id, WorkshopMod.class);
        if (mod == null) {
            Long consumerAppId = workshopFileDetailsService.getModAppId(id);
            if (!Constants.STEAM_ARMA3_ID.equals(consumerAppId)) {
                log.warn("Mod ID {} is not consumed by Arma 3", id);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);// TODO throw custom exception instead
            }

            mod = new WorkshopMod(id);
            mod.setName(workshopFileDetailsService.getModName(id));
            modsDb.save(mod, WorkshopMod.class);
        }

        workshopInstallerService.installOrUpdateMod(getAuth(), mod);

        return new ResponseEntity<>(mod, HttpStatus.OK);
    }

    @DeleteMapping("/uninstall/{id}")
    public ResponseEntity<WorkshopMod> uninstallMod(@PathVariable Long id) {
        log.info("Uninstalling mod {}", id);

        WorkshopMod mod = modsDb.find(id, WorkshopMod.class);
        if (mod != null) {
            workshopInstallerService.deleteMod(mod);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/setActive/{id}")
    public ResponseEntity<WorkshopMod> setActive(@PathVariable Long id, @RequestParam boolean active) {
        log.info("Received request to {} mod {}", (active ? "activate" : "deactivate"), id);

        WorkshopMod mod = modsDb.find(id, WorkshopMod.class);
        if (mod == null) {
            log.info("Mod id {} not found", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        mod.setActive(active);
        modsDb.save(mod, WorkshopMod.class);

        log.info("Mod {} successfully {}", id, (active ? "activated" : "deactivated"));
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PostMapping("/updateAll")
    public ResponseEntity<WorkshopMod> refreshMods() {
        log.info("Refreshing all mods");

        if (!workshopInstallerService.updateAllMods(getAuth()))
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private SteamAuth getAuth() {
        return steamAuthDb.find(Constants.ACCOUND_DEFAULT_ID, SteamAuth.class);
    }

    @Autowired
    public void setWorkshopFileDetailsService(WorkshopFileDetailsService workshopFileDetailsService) {
        this.workshopFileDetailsService = workshopFileDetailsService;
    }

    @Autowired
    public void setWorkshopInstallerService(WorkshopInstallerService workshopInstallerService) {
        this.workshopInstallerService = workshopInstallerService;
    }

    @Autowired
    public void setModsDb(JsonDbService<WorkshopMod> modsDb) {
        this.modsDb = modsDb;
    }

    @Autowired
    public void setSteamAuthDb(JsonDbService<SteamAuth> steamAuthDb) {
        this.steamAuthDb = steamAuthDb;
    }
}
