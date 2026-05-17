package cz.forgottenempire.servermanager.e2e;

import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.common.ProcessFactory;
import cz.forgottenempire.servermanager.installation.ServerInstallation;
import cz.forgottenempire.servermanager.installation.TestRunService;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Primary
@Profile("e2e")
class NoOpTestRunService extends TestRunService {

    NoOpTestRunService(ProcessFactory processFactory, PathsFactory pathsFactory) {
        super(processFactory, pathsFactory);
    }

    @Override
    public synchronized void performServerDryRun(ServerInstallation serverInstallation) {
        // No-op: dry-run requires a real server binary; skip in E2E mode.
    }
}
