package cz.forgottenempire.servermanager.serverinstance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
class HeadlessClientService {

    private final ServerProcessRepository processRepository;

    @Autowired
    public HeadlessClientService(ServerProcessRepository processRepository) {
        this.processRepository = processRepository;
    }

    public void addHeadlessClient(Long id) {
        Arma3ServerProcess serverProcess = getServerProcess(id);
        serverProcess.addHeadlessClient();
    }

    public void removeHeadlessClient(Long id) {
        Arma3ServerProcess serverProcess = getServerProcess(id);
        serverProcess.removeHeadlessClient();
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
