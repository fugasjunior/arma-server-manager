package cz.forgottenempire.servermanager.steamcmd.outputprocessor;

import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class SteamCmdItemInfoRepository {
    private final Map<Long, SteamCmdItemInfo> itemInfos = new ConcurrentHashMap<>();

    public void store(long id, SteamCmdItemInfo itemInfo) {
        itemInfos.put(id, itemInfo);
    }

    public Map<Long, SteamCmdItemInfo> getAll() {
        return Collections.unmodifiableMap(itemInfos);
    }
}
