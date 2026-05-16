package cz.forgottenempire.servermanager.serverinstance.headlessclient;

import cz.forgottenempire.servermanager.serverinstance.ServerRepository;
import cz.forgottenempire.servermanager.serverinstance.entities.Arma3Server;
import cz.forgottenempire.servermanager.serverinstance.process.Arma3ServerProcess;
import cz.forgottenempire.servermanager.serverinstance.process.ServerProcess;
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

    public void addHeadlessClient(Long id) {
        Arma3Server server = getArma3Server(id);
        Arma3ServerProcess serverProcess = getServerProcess(id);
        serverProcess.addHeadlessClient(server);
    }

    public void removeHeadlessClient(Long id) {
        Arma3ServerProcess serverProcess = getServerProcess(id);
        serverProcess.removeHeadlessClient();
    }

    private Arma3Server getArma3Server(Long id) {
        return serverRepository.findById(id)
                .filter(s -> s instanceof Arma3Server)
                .map(s -> (Arma3Server) s)
                .orElseThrow(() -> new IllegalArgumentException("Server ID " + id + " is not Arma 3 server"));
    }

    private Arma3ServerProcess getServerProcess(Long id) {
        ServerProcess serverProcess = processRepository.get(id)
                .orElseThrow(() -> new IllegalStateException("Server ID " + id + " is not running"));
        if (!(serverProcess instanceof Arma3ServerProcess arma3ServerProcess)) {
            throw new IllegalArgumentException("Server ID " + id + " is not Arma 3 server");
        }
        return arma3ServerProcess;
    }
}
