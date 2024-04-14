package cz.forgottenempire.servermanager.installation;

import cz.forgottenempire.servermanager.common.InstallationStatus;
import cz.forgottenempire.servermanager.steamcmd.ErrorStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

import static cz.forgottenempire.servermanager.common.InstallationStatus.*;

@Component
class FailedInstallationDetector {
    private final ServerInstallationRepository repository;

    @Autowired
    FailedInstallationDetector(ServerInstallationRepository repository) {
        this.repository = repository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void setErrorStatusOnInterruptedInstallations() {
        List<ServerInstallation> interruptedInstallations = repository.findAllByInstallationStatus(INSTALLATION_IN_PROGRESS);
        for (ServerInstallation installation : interruptedInstallations) {
            installation.setInstallationStatus(ERROR);
            installation.setErrorStatus(ErrorStatus.INTERRUPTED);
        }
        repository.saveAll(interruptedInstallations);
    }
}
