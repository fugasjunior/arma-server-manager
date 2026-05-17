package cz.forgottenempire.servermanager.e2e;

import cz.forgottenempire.servermanager.api.model.SteamAuthDto;
import cz.forgottenempire.servermanager.steamauth.AuthVerificationResult;
import cz.forgottenempire.servermanager.steamauth.AuthVerificationResult.AuthStatus;
import cz.forgottenempire.servermanager.steamauth.AuthVerificationResult.AuthType;
import cz.forgottenempire.servermanager.steamauth.SteamAuthVerifier;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
@Primary
@Profile("e2e")
class FakeSteamAuthVerifier implements SteamAuthVerifier {

    private final BlockingQueue<AuthVerificationResult> queue = new LinkedBlockingQueue<>();

    void script(AuthVerificationResult result) {
        queue.add(result);
    }

    void reset() {
        queue.clear();
    }

    @Override
    public AuthVerificationResult verifyCredentials(SteamAuthDto authDto) {
        AuthVerificationResult scripted = queue.poll();
        if (scripted != null) {
            return scripted;
        }
        return AuthVerificationResult.builder()
                .status(AuthStatus.SUCCESS)
                .authType(AuthType.NONE)
                .message(null)
                .build();
    }
}
