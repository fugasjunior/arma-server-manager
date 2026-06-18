package cz.forgottenempire.servermanager.e2e;

import cz.forgottenempire.servermanager.api.model.AuthType;
import cz.forgottenempire.servermanager.api.model.SteamLoginResult;
import cz.forgottenempire.servermanager.common.InstallationStatus;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.steamauth.SteamLoginServiceResult;
import cz.forgottenempire.servermanager.support.fakes.FakeProcess;
import cz.forgottenempire.servermanager.support.fakes.FakeProcessFactory;
import cz.forgottenempire.servermanager.workshop.WorkshopMod;
import cz.forgottenempire.servermanager.workshop.WorkshopModsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Test-only endpoints for resetting application state between E2E tests.
 * Only available when the "e2e" Spring profile is active.
 */
@RestController
@Profile("e2e")
@RequestMapping("/test")
public class E2ETestController {

    @Autowired
    private E2EResetService resetService;

    @Autowired
    private FakeProcessFactory fakeProcessFactory;

    @Autowired
    private FakeSteamLoginService fakeSteamLoginService;

    @Autowired
    private WorkshopModsService workshopModsService;

    @PostMapping("/reset")
    public ResponseEntity<Void> reset(
            @RequestParam(defaultValue = "true") boolean seedSteamAuth) {
        resetService.reset(seedSteamAuth);
        fakeSteamLoginService.reset();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/fakes/steam-auth-verify")
    public ResponseEntity<Void> scriptSteamAuthVerify(@RequestBody SteamAuthVerifyRequest request) {
        SteamLoginResult result = SteamLoginResult.valueOf(request.result());
        AuthType authType = request.authType() != null ? AuthType.valueOf(request.authType()) : AuthType.NONE;
        fakeSteamLoginService.script(new SteamLoginServiceResult(result, authType, request.message()));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/seed/workshop-mod")
    public ResponseEntity<Void> seedWorkshopMod(
            @RequestParam long id,
            @RequestParam(defaultValue = "CBA_A3") String name,
            @RequestParam(defaultValue = "ARMA3") String serverType) {
        WorkshopMod mod = new WorkshopMod(id);
        mod.setName(name);
        mod.setServerType(ServerType.valueOf(serverType));
        mod.setInstallationStatus(InstallationStatus.FINISHED);
        workshopModsService.saveMod(mod);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/seed/installed")
    public ResponseEntity<Void> seedInstalled(@RequestParam String type) {
        resetService.markInstalled(type);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/seed/local-mod")
    public ResponseEntity<Void> seedLocalMod(@RequestParam String name, @RequestParam String serverType) throws IOException {
        resetService.seedLocalMod(name, serverType);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/seed/local-mod")
    public ResponseEntity<Void> removeLocalMod(@RequestParam String name, @RequestParam String serverType) throws IOException {
        resetService.removeLocalMod(name, serverType);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/fakes/steamcmd")
    public ResponseEntity<Void> scriptSteamCmd(@RequestBody FakeProcessRequest request) {
        fakeProcessFactory.scriptSteamCmd(buildFakeProcess(request));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/fakes/server-process")
    public ResponseEntity<Void> scriptServerProcess(@RequestBody FakeProcessRequest request) {
        fakeProcessFactory.scriptServerProcess(buildFakeProcess(request));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/fakes/steamcmd/terminate")
    public ResponseEntity<Void> terminateSteamCmd() {
        fakeProcessFactory.terminateCurrentSteamCmd();
        return ResponseEntity.ok().build();
    }

    private FakeProcess buildFakeProcess(FakeProcessRequest request) {
        if (request.alive()) {
            return FakeProcess.stayingAlive();
        }
        if (request.output() != null && !request.output().isBlank()) {
            return FakeProcess.withOutput(request.output(), request.exitCode());
        }
        return FakeProcess.exiting(request.exitCode());
    }

    record FakeProcessRequest(boolean alive, int exitCode, String output) {
        FakeProcessRequest {
            if (output == null) output = "";
        }
    }

    record SteamAuthVerifyRequest(String result, String authType, String message) {}
}
