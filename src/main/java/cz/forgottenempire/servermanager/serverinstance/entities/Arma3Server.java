package cz.forgottenempire.servermanager.serverinstance.entities;

import cz.forgottenempire.servermanager.common.Constants;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.serverinstance.ServerConfig;
import cz.forgottenempire.servermanager.util.SystemUtils;
import cz.forgottenempire.servermanager.workshop.Arma3CDLC;
import cz.forgottenempire.servermanager.workshop.WorkshopMod;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Configurable
public class Arma3Server extends Server {

    @Value("${additionalMods:#{null}}")
    private transient String[] additionalMods;

    private boolean clientFilePatching;
    private boolean serverFilePatching;

    private boolean persistent;

    private boolean battlEye;
    private boolean vonEnabled;
    private boolean verifySignatures;

    @Column(columnDefinition = "LONGTEXT")
    private String additionalOptions;

    @ManyToMany
    private List<WorkshopMod> activeMods;

    @ElementCollection(targetClass = Arma3CDLC.class)
    @Enumerated(EnumType.STRING)
    private List<Arma3CDLC> activeDLCs;

    @OneToOne(mappedBy = "server", cascade = CascadeType.ALL, optional = false, orphanRemoval = true)
    private Arma3DifficultySettings difficultySettings;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "network_settings_id")
    private Arma3NetworkSettings networkSettings;

    @Override
    public void setQueryPort(int queryPort) {
        super.setQueryPort(getPort() + 1);
    }

    @Override
    public List<String> getLaunchParameters() {
        List<String> parameters = new ArrayList<>();
        parameters.add("-port=" + getPort());
        parameters.add("-config=" + getConfigFile().getAbsolutePath());
        parameters.add("-profiles=\"" + getProfilesDirectoryPath() + "\"");
        parameters.add("-name=" + ServerType.ARMA3 + "_" + getId());
        parameters.add("-nosplash");
        parameters.add("-skipIntro");
        parameters.add("-world=empty");
        addModsAndDlcsToParameters(parameters);
        return parameters;
    }

    @Override
    public Collection<ServerConfig> getConfigFiles() {
        return List.of(
                new ServerConfig(getConfigFile(), Constants.SERVER_CONFIG_TEMPLATES.get(ServerType.ARMA3), this),
                new ServerConfig(getProfileFile(), Constants.ARMA3_PROFILE_TEMPLATE, difficultySettings)
        );
    }

    private File getConfigFile() {
        String fileName = "ARMA3_" + getId() + ".cfg";
        return pathsFactory.getConfigFilePath(ServerType.ARMA3, fileName).toFile();
    }

    private File getProfileFile() {
        String serverProfile = "ARMA3_" + getId();
        String profileSubdirectory = SystemUtils.getOsType() == SystemUtils.OSType.WINDOWS ? "Users" : "home";
        return Path.of(getProfilesDirectoryPath(), profileSubdirectory,
                serverProfile, serverProfile + ".Arma3Profile").toFile();
    }

    private String getProfilesDirectoryPath() {
        return pathsFactory.getProfilesDirectoryPath().toString();
    }

    private void addModsAndDlcsToParameters(List<String> parameters) {
        getActiveMods().stream()
                .map(mod -> "-mod=" + mod.getNormalizedName())
                .forEach(parameters::add);
        if (additionalMods != null) {
            Arrays.stream(additionalMods)
                    .map(mod -> "-mod=" + mod)
                    .forEach(parameters::add);
        }
        getActiveDLCs().stream()
                .map(dlc -> "-mod=" + dlc.getId())
                .forEach(parameters::add);
    }
}
