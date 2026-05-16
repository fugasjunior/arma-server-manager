package cz.forgottenempire.servermanager.serverinstance.process;

import cz.forgottenempire.servermanager.serverinstance.ServerLaunchContext;
import cz.forgottenempire.servermanager.serverinstance.entities.Arma3Server;
import cz.forgottenempire.servermanager.serverinstance.entities.Server;
import cz.forgottenempire.servermanager.serverinstance.headlessclient.HeadlessClient;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class Arma3ServerProcess extends ServerProcess {

    private final String[] additionalMods;
    private final Deque<HeadlessClient> headlessClients;

    public Arma3ServerProcess(long serverId, ServerProcessCreator serverProcessCreator,
                              ServerLaunchContext launchContext, String[] additionalMods) {
        super(serverId, serverProcessCreator, launchContext);
        this.additionalMods = additionalMods;
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
    public void restart(Server server) {
        int countOfHeadlessClients = headlessClients.size();
        super.restart(server);
        Arma3Server arma3Server = (Arma3Server) server;
        for (int i = 0; i < countOfHeadlessClients; i++) {
            addHeadlessClient(arma3Server);
        }
    }

    public void addHeadlessClient(Arma3Server server) {
        headlessClients.push(
                new HeadlessClient(headlessClients.size() + 1, server, launchContext.pathsFactory(),
                        serverProcessCreator, additionalMods).start());
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
