package cz.forgottenempire.arma3servergui.installation;

import cz.forgottenempire.arma3servergui.common.ServerType;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
class ServerInstallationServiceImpl implements ServerInstallationService {

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
