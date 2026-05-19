package cz.forgottenempire.servermanager.serverinstance.entities;

import cz.forgottenempire.servermanager.common.Constants;
import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.serverinstance.ServerConfig;
import cz.forgottenempire.servermanager.serverinstance.ServerLaunchContext;
import cz.forgottenempire.servermanager.util.SystemUtils;
import cz.forgottenempire.servermanager.workshop.Arma3CDLC;
import cz.forgottenempire.servermanager.workshop.WorkshopMod;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "arma3server")
public class Arma3Server extends Server {

    private boolean clientFilePatching;
    private boolean serverFilePatching;

    private int targetHeadlessClientsCount;

    private boolean persistent;

    private boolean battlEye;
    private boolean vonEnabled;
    private boolean verifySignatures;

    @Column(columnDefinition = "LONGTEXT")
    private String additionalOptions;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "arma3server_active_mods",
            joinColumns = @JoinColumn(name = "arma3server_id"),
            inverseJoinColumns = @JoinColumn(name = "active_mods_id"))
    @OrderColumn(name = "mod_order")
    private List<WorkshopMod> activeMods;

    @ElementCollection(targetClass = Arma3CDLC.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "arma3server_activedlcs", joinColumns = @JoinColumn(name = "arma3server_id"))
    @Column(name = "activedlcs")
    @OrderColumn(name = "dlc_order")
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
    public List<String> getLaunchParameters(ServerLaunchContext ctx) {
        List<String> parameters = new ArrayList<>();
        parameters.add("-port=" + getPort());
        parameters.add("-config=\"" + getConfigFile(ctx.pathsFactory()).getAbsolutePath() + "\"");

        if (networkSettings != null) {
            parameters.add("-cfg=\"" + getNetworkConfigFile(ctx.pathsFactory()).getAbsolutePath() + "\"");
        }

        parameters.add("-profiles=\"" + getProfilesDirectoryPath(ctx.pathsFactory()) + "\"");
        parameters.add("-name=" + ServerType.ARMA3 + "_" + getId());
        parameters.add("-nosplash");
        parameters.add("-skipIntro");
        parameters.add("-world=empty");
        parameters.addAll(getModsAsParameters(ctx.additionalMods()));
        addCustomLaunchParameters(parameters);
        return parameters;
    }

    @Override
    public Collection<ServerConfig> getConfigFiles(ServerLaunchContext ctx) {
        List<ServerConfig> configs = new ArrayList<>();
        configs.add(new ServerConfig(getConfigFile(ctx.pathsFactory()), Constants.SERVER_CONFIG_TEMPLATES.get(ServerType.ARMA3), this, ctx.freeMarkerConfigurer()));
        configs.add(new ServerConfig(
                getProfileFile(ctx.pathsFactory()),
                Constants.ARMA3_PROFILE_TEMPLATE,
                Objects.requireNonNullElseGet(difficultySettings, Arma3DifficultySettings::new),
                ctx.freeMarkerConfigurer())
        );
        if (networkSettings != null) {
            configs.add(new ServerConfig(getNetworkConfigFile(ctx.pathsFactory()), Constants.ARMA3_NETWORK_SETTINGS, networkSettings, ctx.freeMarkerConfigurer()));
        }
        return configs;
    }

    public List<String> getModsAsParameters(String[] additionalMods) {
        return Stream.of(
                        getServerModsAsParameters(),
                        getClientModsAsParameters(),
                        getCreatorDlcsAsParameters(),
                        getAdditionalModsAsParameters(additionalMods)
                )
                .flatMap(Function.identity())
                .toList();
    }

    public Stream<String> getServerModsAsParameters() {
        return getActiveServerMods().stream().map(mod -> "-serverMod=" + mod.getNormalizedName());
    }

    public Stream<String> getClientModsAsParameters() {
        return getActiveClientMods().stream().map(mod -> "-mod=" + mod.getNormalizedName());
    }

    public Stream<String> getCreatorDlcsAsParameters() {
        return getActiveDLCs().stream().map(dlc -> "-mod=" + dlc.getId());
    }

    public Stream<String> getAdditionalModsAsParameters(String[] additionalMods) {
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

    private File getConfigFile(PathsFactory pathsFactory) {
        String fileName = "ARMA3_" + getId() + ".cfg";
        return pathsFactory.getConfigFilePath(ServerType.ARMA3, fileName).toFile();
    }

    private File getNetworkConfigFile(PathsFactory pathsFactory) {
        String fileName = "ARMA3_" + getId() + "_network.cfg";
        return pathsFactory.getConfigFilePath(ServerType.ARMA3, fileName).toFile();
    }

    private File getProfileFile(PathsFactory pathsFactory) {
        String serverProfile = "ARMA3_" + getId();
        String profileSubdirectory = SystemUtils.getOsType() == SystemUtils.OSType.WINDOWS ? "Users" : "home";
        return Path.of(getProfilesDirectoryPath(pathsFactory), profileSubdirectory,
                serverProfile, serverProfile + ".Arma3Profile").toFile();
    }

    private String getProfilesDirectoryPath(PathsFactory pathsFactory) {
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
