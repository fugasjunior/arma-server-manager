package cz.forgottenempire.servermanager.steamcmd;

import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.serverinstance.LogFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
class SteamCmdLogsService {
    private final PathsFactory pathsFactory;

    @Autowired
    SteamCmdLogsService(PathsFactory pathsFactory) {
        this.pathsFactory = pathsFactory;
    }

    LogFile getLogFile() {
        return new LogFile(pathsFactory.getSteamCmdLogFile());
    }
}
