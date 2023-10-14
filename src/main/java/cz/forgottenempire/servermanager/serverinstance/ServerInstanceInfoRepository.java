package cz.forgottenempire.servermanager.serverinstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
class ServerInstanceInfoRepository {

    private final Map<Long, ServerInstanceInfo> instanceInfoMap = new ConcurrentHashMap<>();

    public List<ServerInstanceInfo> getAll() {
        return new ArrayList<>(instanceInfoMap.values());
    }

    public void storeServerInstanceInfo(Long id, ServerInstanceInfo instanceInfo) {
        instanceInfoMap.put(id, instanceInfo);
    }
}
