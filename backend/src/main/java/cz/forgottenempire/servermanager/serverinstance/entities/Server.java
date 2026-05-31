package cz.forgottenempire.servermanager.serverinstance.entities;

import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.serverinstance.ServerConfig;
import cz.forgottenempire.servermanager.serverinstance.LogFile;
import cz.forgottenempire.servermanager.serverinstance.ServerLaunchContext;
import java.io.IOException;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Server {

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

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "server")
    private List<LaunchParameter> customLaunchParameters = new ArrayList<>();

    private String password;
    private String adminPassword;

    @Column(name = "automatic_restart")
    private boolean restartAutomatically;
    @Column(name = "automatic_restart_time")
    private LocalTime automaticRestartTime;

    public abstract List<String> getLaunchParameters(ServerLaunchContext ctx);

    public abstract Collection<ServerConfig> getConfigFiles(ServerLaunchContext ctx);

    /**
     * Hook called before config generation and process start.
     * Subclasses override to create instance-specific directories or perform other setup.
     * Default: no-op.
     */
    public void prepareLaunchEnvironment(ServerLaunchContext ctx) throws IOException {
    }

    public LogFile getLog(PathsFactory pathsFactory) {
        return new LogFile(pathsFactory.getServerLogFile(type, id));
    }
}
