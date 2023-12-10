package cz.forgottenempire.servermanager.serverinstance.process;

import cz.forgottenempire.servermanager.serverinstance.ServerInstanceService;
import cz.forgottenempire.servermanager.serverinstance.entities.Arma3Server;
import cz.forgottenempire.servermanager.serverinstance.entities.Server;
import cz.forgottenempire.servermanager.serverinstance.headlessclient.HeadlessClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Deque;
import java.util.LinkedList;

@Configurable
public class Arma3ServerProcess extends ServerProcess {
    private final long serverId;
    private final Deque<HeadlessClient> headlessClients;

    private ServerInstanceService serverInstanceService;

    public Arma3ServerProcess(long serverId) {
        super(serverId);
        headlessClients = new LinkedList<>();
        this.serverId = serverId;
    }

    @Override
    public void stop() {
        super.stop();
        for (int i = 0; i < headlessClients.size(); i++) {
            removeHeadlessClient();
        }
    }

    @Override
    public void restart() {
        int countOfHeadlessClients = headlessClients.size();
        super.restart();
        for (int i = 0; i < countOfHeadlessClients; i++) {
            addHeadlessClient();
        }
    }

    public void addHeadlessClient() {
        Server server = serverInstanceService.getServer(serverId).orElseThrow();
        if (!(server instanceof Arma3Server arma3Server)) {
            throw new IllegalStateException("Server ID " + server + " is not Arma 3 server");
        }
        headlessClients.push(new HeadlessClient(headlessClients.size() + 1, arma3Server).start());
        instanceInfo.setHeadlessClientsCount(headlessClients.size());
    }

    public void removeHeadlessClient() {
        if (headlessClients.isEmpty()) {
            return;
        }
        headlessClients.pop().stop();
        instanceInfo.setHeadlessClientsCount(headlessClients.size());
    }

    @Autowired
    void setServerInstanceService(ServerInstanceService serverInstanceService) {
        this.serverInstanceService = serverInstanceService;
    }
}
