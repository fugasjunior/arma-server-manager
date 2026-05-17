package cz.forgottenempire.servermanager.e2e;

import cz.forgottenempire.servermanager.steamauth.AuthVerificationResult;
import cz.forgottenempire.servermanager.steamauth.AuthVerificationResult.AuthStatus;
import cz.forgottenempire.servermanager.steamauth.AuthVerificationResult.AuthType;
import cz.forgottenempire.servermanager.support.fakes.FakeProcess;
import cz.forgottenempire.servermanager.support.fakes.FakeProcessFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    private FakeSteamAuthVerifier fakeSteamAuthVerifier;

    @PostMapping("/reset")
    public ResponseEntity<Void> reset(
            @RequestParam(defaultValue = "true") boolean seedSteamAuth) {
        resetService.reset(seedSteamAuth);
        fakeSteamAuthVerifier.reset();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/fakes/steam-auth-verify")
    public ResponseEntity<Void> scriptSteamAuthVerify(@RequestBody SteamAuthVerifyRequest request) {
        AuthStatus status = AuthStatus.valueOf(request.status());
        AuthType authType = request.authType() != null ? AuthType.valueOf(request.authType()) : AuthType.NONE;
        fakeSteamAuthVerifier.script(AuthVerificationResult.builder()
                .status(status)
                .authType(authType)
                .message(request.message())
                .build());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/seed/installed")
    public ResponseEntity<Void> seedInstalled(@RequestParam String type) {
        resetService.markInstalled(type);
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

    record SteamAuthVerifyRequest(String status, String authType, String message) {}
}
