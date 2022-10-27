package cz.forgottenempire.arma3servergui.server.installation.services.impl;

import cz.forgottenempire.arma3servergui.common.exceptions.NotFoundException;
import cz.forgottenempire.arma3servergui.server.ServerType;
import cz.forgottenempire.arma3servergui.server.installation.entities.ServerInstallation;
import cz.forgottenempire.arma3servergui.server.installation.repositories.ServerInstallationRepository;
import cz.forgottenempire.arma3servergui.server.installation.services.ServerInstallationService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServerInstallationServiceImpl implements ServerInstallationService {

    private final ServerInstallationRepository installationRepository;

    @Autowired
    public ServerInstallationServiceImpl(ServerInstallationRepository installationRepository) {
        this.installationRepository = installationRepository;
    }

    @Override
    public List<ServerInstallation> getAllServerInstallations() {
        return ServerType.getAll().stream()
                .map(this::getServerInstallation)
                .toList();
    }

    @Override
    public ServerInstallation getServerInstallation(ServerType type) {
        return installationRepository.findById(type)
                .orElseGet(() -> installationRepository.save(new ServerInstallation(type)));
    }
}
