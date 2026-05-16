package cz.forgottenempire.servermanager.serverinstance.process;

import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.serverinstance.ServerRepository;
import cz.forgottenempire.servermanager.serverinstance.entities.Arma3Server;
import cz.forgottenempire.servermanager.serverinstance.entities.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Clock;

@Component
public class ServerProcessFactory {

    private final ServerProcessCreator serverProcessCreator;
    private final PathsFactory pathsFactory;
    private final ServerRepository serverRepository;
    private final Clock clock;
    private final TaskScheduler taskScheduler;

    @Autowired
    public ServerProcessFactory(ServerProcessCreator serverProcessCreator, PathsFactory pathsFactory,
                                ServerRepository serverRepository, Clock clock, TaskScheduler taskScheduler) {
        this.serverProcessCreator = serverProcessCreator;
        this.pathsFactory = pathsFactory;
        this.serverRepository = serverRepository;
        this.clock = clock;
        this.taskScheduler = taskScheduler;
    }

    public ServerProcess create(Server server) {
        if (server instanceof Arma3Server) {
            return new Arma3ServerProcess(server.getId(), serverProcessCreator, pathsFactory,
                    serverRepository, clock, taskScheduler);
        }
        return new ServerProcess(server.getId(), serverProcessCreator, pathsFactory,
                serverRepository, clock, taskScheduler);
    }
}
