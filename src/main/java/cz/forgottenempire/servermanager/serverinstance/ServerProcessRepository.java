package cz.forgottenempire.servermanager.serverinstance;

import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
class ServerProcessRepository {

    private final Map<Long, ServerProcess> serverIdToProcessMap = new ConcurrentHashMap<>();

    public Collection<ServerProcess> getAll() {
        return serverIdToProcessMap.values();
    }

    public Optional<ServerProcess> get(long serverId) {
        return Optional.ofNullable(serverIdToProcessMap.get(serverId));
    }

    public void store(long serverId, ServerProcess process) {
        serverIdToProcessMap.put(serverId, process);
    }
}
