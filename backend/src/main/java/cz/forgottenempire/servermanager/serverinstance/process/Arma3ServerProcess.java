package cz.forgottenempire.servermanager.serverinstance.process;

import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.serverinstance.ServerRepository;
import cz.forgottenempire.servermanager.serverinstance.entities.Arma3Server;
import cz.forgottenempire.servermanager.serverinstance.entities.Server;
import cz.forgottenempire.servermanager.serverinstance.headlessclient.HeadlessClient;
import org.springframework.scheduling.TaskScheduler;

import java.time.Clock;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class Arma3ServerProcess extends ServerProcess {

    private final Deque<HeadlessClient> headlessClients;

    public Arma3ServerProcess(long serverId, ServerProcessCreator serverProcessCreator, PathsFactory pathsFactory,
                              ServerRepository serverRepository, Clock clock, TaskScheduler taskScheduler) {
        super(serverId, serverProcessCreator, pathsFactory, serverRepository, clock, taskScheduler);
        headlessClients = new LinkedList<>();
    }

    @Override
    public void stop() {
        super.stop();
        while (!headlessClients.isEmpty()) {
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
        Server server = serverRepository.findById(getServerId()).orElseThrow();
        if (!(server instanceof Arma3Server arma3Server)) {
            throw new IllegalStateException("Server ID " + server.getId() + " is not Arma 3 server");
        }
        headlessClients.push(new HeadlessClient(headlessClients.size() + 1, arma3Server, pathsFactory, serverProcessCreator).start());
        instanceInfo.setHeadlessClientsCount(headlessClients.size());
    }

    public void removeHeadlessClient() {
        if (headlessClients.isEmpty()) {
            return;
        }
        headlessClients.pop().stop();
        instanceInfo.setHeadlessClientsCount(headlessClients.size());
    }

    public void checkHeadlessClients() {
        List<HeadlessClient> crashedHeadlessClients = headlessClients.stream().filter(hc -> !hc.isAlive()).toList();
        crashedHeadlessClients.forEach(headlessClients::remove);
        instanceInfo.setHeadlessClientsCount(headlessClients.size());
    }
}
