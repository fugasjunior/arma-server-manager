package cz.forgottenempire.servermanager.serverinstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
class ServerInstanceInfoRepository {

    private final Map<Long, ServerInstanceInfo> instanceInfoMap = new ConcurrentHashMap<>();

    public List<ServerInstanceInfo> getAll() {
        return new ArrayList<>(instanceInfoMap.values());
    }

    public ServerInstanceInfo getServerInstanceInfo(Long id) {
        return Optional.ofNullable(instanceInfoMap.get(id)).orElseGet(() -> {
            ServerInstanceInfo instanceInfo = createEmptyInstanceInfo(id);
            instanceInfoMap.put(id, instanceInfo);
            return instanceInfo;
        });
    }

    public void storeServerInstanceInfo(Long id, ServerInstanceInfo instanceInfo) {
        instanceInfoMap.put(id, instanceInfo);
    }

    private ServerInstanceInfo createEmptyInstanceInfo(Long id) {
        return ServerInstanceInfo.builder()
                .id(id)
                .startedAt(null)
                .playersOnline(0)
                .process(null)
                .version("")
                .map("")
                .build();
    }
}
