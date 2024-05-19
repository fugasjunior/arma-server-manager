package cz.forgottenempire.servermanager.steamcmd;

import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.serverinstance.ServerLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
class SteamCmdLogsService {
    private final PathsFactory pathsFactory;

    @Autowired
    SteamCmdLogsService(PathsFactory pathsFactory) {
        this.pathsFactory = pathsFactory;
    }

    ServerLog getLogFile() {
        return new ServerLog(pathsFactory.getSteamCmdLogFile());
    }
}
