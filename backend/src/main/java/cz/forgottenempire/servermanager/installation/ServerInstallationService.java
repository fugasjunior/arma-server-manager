package cz.forgottenempire.servermanager.installation;

import cz.forgottenempire.servermanager.common.InstallationStatus;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.common.exceptions.NotFoundException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServerInstallationService {

    private final ServerInstallationRepository installationRepository;

    @Autowired
    public ServerInstallationService(ServerInstallationRepository installationRepository) {
        this.installationRepository = installationRepository;
    }

    public List<ServerInstallation> getAvailableServerInstallations() {
        return getAvailableServerTypes().stream()
                .map(this::getServerInstallation)
                .toList();
    }

    public ServerInstallation getServerInstallation(ServerType type) {
        return installationRepository.findById(type)
                .orElseThrow(() -> new NotFoundException("Server of type '" + type + "' not found."));
    }

    public boolean isServerInstalled(ServerType type) {
        return getServerInstallation(type).getInstallationStatus() == InstallationStatus.FINISHED;
    }

    public Collection<ServerType> getInstalledRelatedServerTypes(ServerType serverType) {
        Set<ServerType> serverTypes = new HashSet<>();
        if (isServerInstalled(serverType)) {
            serverTypes.add(serverType);
        }
        if (serverType == ServerType.DAYZ && isServerInstalled(ServerType.DAYZ_EXP)) {
            serverTypes.add(ServerType.DAYZ_EXP);
        }
        return serverTypes;
    }

    public void setServerBranch(ServerInstallation serverInstallation, ServerInstallation.Branch branch) {
        serverInstallation.setBranch(branch);
        installationRepository.save(serverInstallation);
    }

    private List<ServerType> getAvailableServerTypes() {
        return ServerType.getAll();
    }
}
