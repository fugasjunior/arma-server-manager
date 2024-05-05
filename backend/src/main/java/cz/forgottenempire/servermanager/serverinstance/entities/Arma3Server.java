package cz.forgottenempire.servermanager.serverinstance.entities;

import cz.forgottenempire.servermanager.common.Constants;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.serverinstance.process.Arma3ServerProcess;
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
import java.util.stream.Stream;

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

    @ManyToMany(fetch = FetchType.EAGER)
    private List<WorkshopMod> activeMods;

    @ElementCollection(targetClass = Arma3CDLC.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private List<Arma3CDLC> activeDLCs;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "difficulty_settings_id")
    private Arma3DifficultySettings difficultySettings;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "network_settings_id")
    private Arma3NetworkSettings networkSettings;

    @Override
    public void setQueryPort(int queryPort) {
        super.setQueryPort(getPort() + 1);
    }

    @Override
    public Arma3ServerProcess getProcess() {
        return new Arma3ServerProcess(getId());
    }

    @Override
    public List<String> getLaunchParameters() {
        List<String> parameters = new ArrayList<>();
        parameters.add("-port=" + getPort());
        parameters.add("-config=\"" + getConfigFile().getAbsolutePath() + "\"");

        if (networkSettings != null) {
            parameters.add("-cfg=\"" + getNetworkConfigFile().getAbsolutePath() + "\"");
        }

        parameters.add("-profiles=\"" + getProfilesDirectoryPath() + "\"");
        parameters.add("-name=" + ServerType.ARMA3 + "_" + getId());
        parameters.add("-nosplash");
        parameters.add("-skipIntro");
        parameters.add("-world=empty");
        parameters.addAll(getModsAsParameters());
        addCustomLaunchParameters(parameters);
        return parameters;
    }

    @Override
    public Collection<ServerConfig> getConfigFiles() {
        List<ServerConfig> configs = new ArrayList<>();
        configs.add(new ServerConfig(getConfigFile(), Constants.SERVER_CONFIG_TEMPLATES.get(ServerType.ARMA3), this));
        configs.add(new ServerConfig(getProfileFile(), Constants.ARMA3_PROFILE_TEMPLATE, difficultySettings));
        if (networkSettings != null) {
            configs.add(new ServerConfig(getNetworkConfigFile(), Constants.ARMA3_NETWORK_SETTINGS, networkSettings));
        }
        return configs;
    }

    public List<String> getModsAsParameters() {
        return Stream.concat(
                Stream.concat(
                        getWorkshopModsAsParameters(),
                        getCreatorDlcsAsParameters()
                ),
                getAdditionalModsAsParameters()
        ).toList();
    }

    private Stream<String> getWorkshopModsAsParameters() {
        return Stream.concat(
                getActiveServerMods().stream().map(mod -> "-serverMod=" + mod.getNormalizedName()),
                getActiveClientMods().stream().map(mod -> "-mod=" + mod.getNormalizedName())
        );
    }

    private Stream<String> getCreatorDlcsAsParameters() {
        return getActiveDLCs().stream().map(dlc -> "-mod=" + dlc.getId());
    }

    private Stream<String> getAdditionalModsAsParameters() {
        return Arrays.stream(additionalMods == null ? new String[0] : additionalMods).map(mod -> "-mod=" + mod);
    }

    private List<WorkshopMod> getActiveClientMods() {
        return getActiveMods().stream()
                .filter(mod -> !mod.isServerOnly())
                .toList();
    }

    private List<WorkshopMod> getActiveServerMods() {
        return getActiveMods().stream()
                .filter(WorkshopMod::isServerOnly)
                .toList();
    }

    private File getConfigFile() {
        String fileName = "ARMA3_" + getId() + ".cfg";
        return pathsFactory.getConfigFilePath(ServerType.ARMA3, fileName).toFile();
    }

    private File getNetworkConfigFile() {
        String fileName = "ARMA3_" + getId() + "_network.cfg";
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

    private void addCustomLaunchParameters(List<String> parameters) {
        getCustomLaunchParameters().forEach(parameter -> {
            String s = "-" + parameter.getName();
            if (parameter.getValue() != null) {
                s += "=" + parameter.getValue();
            }
            parameters.add(s);
        });
    }
}
