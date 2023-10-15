package cz.forgottenempire.servermanager.serverinstance.entities;

import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.serverinstance.ServerConfig;
import cz.forgottenempire.servermanager.serverinstance.ServerLog;
import cz.forgottenempire.servermanager.serverinstance.ServerProcess;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Configurable
public abstract class Server {

    protected transient PathsFactory pathsFactory;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated
    @NotNull
    private ServerType type;

    private String description;

    @NotEmpty
    private String name;
    @Min(1)
    private int port;
    @Min(1)
    private int queryPort;
    @Min(1)
    private int maxPlayers;

    private String password;
    private String adminPassword;

    public abstract List<String> getLaunchParameters();
    public abstract Collection<ServerConfig> getConfigFiles();

    public ServerLog getLog() {
        return new ServerLog(pathsFactory.getServerLogFile(type, id));
    }

    public ServerProcess getProcess() {
        return new ServerProcess(this, pathsFactory);
    }

    @Autowired
    void setPathsFactory(PathsFactory pathsFactory) {
        this.pathsFactory = pathsFactory;
    }
}
