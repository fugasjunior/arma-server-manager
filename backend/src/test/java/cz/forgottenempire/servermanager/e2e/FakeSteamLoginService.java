package cz.forgottenempire.servermanager.e2e;

import cz.forgottenempire.servermanager.api.model.AuthType;
import cz.forgottenempire.servermanager.api.model.SteamLoginResult;
import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.common.ProcessFactory;
import cz.forgottenempire.servermanager.steamauth.SteamAuthService;
import cz.forgottenempire.servermanager.steamauth.SteamLoginService;
import cz.forgottenempire.servermanager.steamauth.SteamLoginServiceResult;
import cz.forgottenempire.servermanager.steamauth.SteamSessionStatusHolder;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
@Primary
@Profile("e2e")
class FakeSteamLoginService extends SteamLoginService {

    private final BlockingQueue<SteamLoginServiceResult> queue = new LinkedBlockingQueue<>();

    FakeSteamLoginService(ProcessFactory processFactory, PathsFactory pathsFactory,
                          SteamAuthService authService, SteamSessionStatusHolder sessionStatusHolder) {
        super(processFactory, pathsFactory, authService, sessionStatusHolder);
    }

    void script(SteamLoginServiceResult result) {
        queue.add(result);
    }

    void reset() {
        queue.clear();
    }

    @Override
    public SteamLoginServiceResult login(String username, String password, String steamGuardCode) {
        SteamLoginServiceResult scripted = queue.poll();
        if (scripted != null) {
            return scripted;
        }
        return new SteamLoginServiceResult(SteamLoginResult.SUCCESS, AuthType.NONE, null);
    }
}
