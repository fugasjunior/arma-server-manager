package cz.forgottenempire.arma3servergui.server.additionalserver.repositories;

import cz.forgottenempire.arma3servergui.server.additionalserver.AdditionalServerInstanceInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class AdditionalServerInstanceInfoRepository {

    private final Map<Long, AdditionalServerInstanceInfo> statusMap = new ConcurrentHashMap<>();

    public List<AdditionalServerInstanceInfo> getAll() {
        return new ArrayList<>(statusMap.values());
    }

    public void storeServerInstanceInfo(Long id, AdditionalServerInstanceInfo instanceInfo) {
        statusMap.put(id, instanceInfo);
    }

    public AdditionalServerInstanceInfo getServerInstanceInfo(Long id) {
        return Optional.ofNullable(statusMap.get(id)).orElseGet(() -> {
            AdditionalServerInstanceInfo instanceInfo =
                    new AdditionalServerInstanceInfo(id, false, null, null);
            statusMap.put(id, instanceInfo);
            return instanceInfo;
        });
    }
}
