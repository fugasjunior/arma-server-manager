package cz.forgottenempire.servermanager.serverinstance.headlessclient;

import cz.forgottenempire.servermanager.serverinstance.ServerRepository;
import cz.forgottenempire.servermanager.serverinstance.entities.Arma3Server;
import cz.forgottenempire.servermanager.serverinstance.process.Arma3ServerProcess;
import cz.forgottenempire.servermanager.serverinstance.process.ServerProcessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
class HeadlessClientService {

    private final ServerProcessRepository processRepository;
    private final ServerRepository serverRepository;

    @Autowired
    public HeadlessClientService(ServerProcessRepository processRepository, ServerRepository serverRepository) {
        this.processRepository = processRepository;
        this.serverRepository = serverRepository;
    }

    public void setTargetCount(Long id, int target) {
        if (target < 0) {
            throw new IllegalArgumentException("Target headless client count must be >= 0");
        }
        Arma3Server server = getArma3Server(id);
        server.setTargetHeadlessClientsCount(target);
        serverRepository.save(server);

        processRepository.get(id).ifPresent(p -> {
            if (p instanceof Arma3ServerProcess arma3) arma3.reconcileHeadlessClients(server);
        });
    }

    private Arma3Server getArma3Server(Long id) {
        return serverRepository.findById(id)
                .filter(s -> s instanceof Arma3Server)
                .map(s -> (Arma3Server) s)
                .orElseThrow(() -> new IllegalArgumentException("Server ID " + id + " is not Arma 3 server"));
    }
}
