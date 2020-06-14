package cz.forgottenempire.arma3servergui.services;

public interface AdditionalServersService {
    void startServer(Long serverId);

    void stopServer(Long serverId);

    boolean isAlive(Long serverId);
}
