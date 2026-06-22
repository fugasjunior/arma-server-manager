package cz.forgottenempire.servermanager.serverinstance.process;

import cz.forgottenempire.servermanager.serverinstance.ServerLaunchContext;
import cz.forgottenempire.servermanager.serverinstance.entities.Arma3Server;
import cz.forgottenempire.servermanager.serverinstance.entities.Server;
import cz.forgottenempire.servermanager.serverinstance.headlessclient.HeadlessClient;

import java.nio.file.Path;
import java.util.Deque;
import java.util.LinkedList;

public class Arma3ServerProcess extends ServerProcess {

    private final String[] additionalMods;
    private final int logMaxFiles;
    private final Deque<HeadlessClient> headlessClients;

    public Arma3ServerProcess(long serverId, ServerProcessCreator serverProcessCreator,
                              ServerLaunchContext launchContext, String[] additionalMods, int logMaxFiles) {
        super(serverId, serverProcessCreator, launchContext, logMaxFiles);
        this.additionalMods = additionalMods;
        this.logMaxFiles = logMaxFiles;
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
        super.restart(server);
        reconcileHeadlessClients((Arma3Server) server);
    }

    public void addHeadlessClient(Arma3Server server) {
        int hcId = headlessClients.size() + 1;
        Path profilesPath = launchContext.arma3InstancePaths()
                .getHeadlessClientProfilesPath(server.getId(), hcId);
        headlessClients.push(
                new HeadlessClient(hcId, server, launchContext.pathsFactory(),
                        serverProcessCreator, profilesPath, additionalMods, logMaxFiles).start());
        instanceInfo.setHeadlessClientsCount(headlessClients.size());
    }

    public void removeHeadlessClient() {
        if (headlessClients.isEmpty()) {
            return;
        }
        headlessClients.pop().stop();
        instanceInfo.setHeadlessClientsCount(headlessClients.size());
    }

    public void reconcileHeadlessClients(Arma3Server server) {
        headlessClients.removeIf(hc -> !hc.isAlive());
        // If server process is dead, caller (crash handler) is responsible for stopping remaining HCs via stop()
        if (!isAlive()) {
            instanceInfo.setHeadlessClientsCount(headlessClients.size());
            return;
        }
        int target = server.getTargetHeadlessClientsCount();
        while (headlessClients.size() < target) {
            addHeadlessClient(server);
        }
        while (headlessClients.size() > target) {
            removeHeadlessClient();
        }
        instanceInfo.setHeadlessClientsCount(headlessClients.size());
    }
}
