package cz.forgottenempire.servermanager.serverinstance.entities;

import cz.forgottenempire.servermanager.common.Arma3InstancePaths;
import cz.forgottenempire.servermanager.common.Constants;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.serverinstance.ServerConfig;
import cz.forgottenempire.servermanager.serverinstance.ServerLaunchContext;
import cz.forgottenempire.servermanager.util.SystemUtils;
import cz.forgottenempire.servermanager.workshop.Arma3CDLC;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
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

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "arma3server_id", nullable = false)
    @OrderColumn(name = "mod_order")
    private List<Arma3ServerActiveMod> activeMods = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "arma3server_id", nullable = false)
    @OrderColumn(name = "mod_order")
    private List<Arma3ServerActiveLocalMod> activeLocalMods = new ArrayList<>();

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
        Arma3InstancePaths paths = ctx.arma3InstancePaths();
        List<String> parameters = new ArrayList<>();
        parameters.add("-port=" + getPort());
        parameters.add("-config=\"" + getConfigFile(paths).getAbsolutePath() + "\"");

        if (networkSettings != null) {
            parameters.add("-cfg=\"" + getNetworkConfigFile(paths).getAbsolutePath() + "\"");
        }

        parameters.add("-profiles=\"" + paths.getInstanceProfilesPath(getId()) + "\"");
        parameters.add("-name=" + ServerType.ARMA3 + "_" + getId());
        parameters.add("-mpmissions=\"" + paths.getInstanceMpmissionsPath(getId()) + "\"");
        parameters.add("-keysFolder=\"" + paths.getInstanceKeysPath(getId()) + "\"");
        parameters.add("-nosplash");
        parameters.add("-skipIntro");
        parameters.add("-world=empty");
        parameters.addAll(getModsAsParameters(ctx.additionalMods()));
        addCustomLaunchParameters(parameters);
        return parameters;
    }

    @Override
    public void prepareLaunchEnvironment(ServerLaunchContext ctx) throws IOException {
        Files.createDirectories(ctx.arma3InstancePaths().getInstanceMpmissionsPath(getId()));
        ctx.arma3KeyService().rebuildInstanceBikeys(this); // creates + populates keys/
    }

    @Override
    public Collection<ServerConfig> getConfigFiles(ServerLaunchContext ctx) {
        Arma3InstancePaths paths = ctx.arma3InstancePaths();
        List<ServerConfig> configs = new ArrayList<>();
        configs.add(new ServerConfig(getConfigFile(paths), Constants.SERVER_CONFIG_TEMPLATES.get(ServerType.ARMA3), this, ctx.freeMarkerConfigurer()));
        configs.add(new ServerConfig(
                getProfileFile(paths),
                Constants.ARMA3_PROFILE_TEMPLATE,
                Objects.requireNonNullElseGet(difficultySettings, Arma3DifficultySettings::new),
                ctx.freeMarkerConfigurer())
        );
        if (networkSettings != null) {
            configs.add(new ServerConfig(getNetworkConfigFile(paths), Constants.ARMA3_NETWORK_SETTINGS, networkSettings, ctx.freeMarkerConfigurer()));
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
        return orderedActiveMods()
                .filter(ActiveModEntry::isServerOnly)
                .map(e -> "-serverMod=" + e.getLaunchName());
    }

    public Stream<String> getClientModsAsParameters() {
        return orderedActiveMods()
                .filter(e -> !e.isServerOnly())
                .map(e -> "-mod=" + e.getLaunchName());
    }

    public Stream<String> getHeadlessClientModsAsParameters() {
        return orderedActiveMods()
                .filter(ActiveModEntry::isLoadOnHeadlessClient)
                .map(e -> "-mod=" + e.getLaunchName());
    }

    private Stream<ActiveModEntry> orderedActiveMods() {
        return Stream.concat(
                activeMods.stream().map(e -> (ActiveModEntry) e),
                activeLocalMods.stream().map(e -> (ActiveModEntry) e)
        ).sorted(Comparator.comparingInt(ActiveModEntry::getPosition));
    }

    public Stream<String> getCreatorDlcsAsParameters() {
        return getActiveDLCs().stream().map(dlc -> "-mod=" + dlc.getId());
    }

    public Stream<String> getAdditionalModsAsParameters(String[] additionalMods) {
        return Arrays.stream(additionalMods == null ? new String[0] : additionalMods).map(mod -> "-mod=" + mod);
    }

    private File getConfigFile(Arma3InstancePaths paths) {
        String fileName = Arma3InstancePaths.instanceDirName(getId()) + ".cfg";
        return paths.getInstanceConfigsPath(getId()).resolve(fileName).toAbsolutePath().toFile();
    }

    private File getNetworkConfigFile(Arma3InstancePaths paths) {
        String fileName = Arma3InstancePaths.instanceDirName(getId()) + "_network.cfg";
        return paths.getInstanceConfigsPath(getId()).resolve(fileName).toAbsolutePath().toFile();
    }

    private File getProfileFile(Arma3InstancePaths paths) {
        String serverProfile = Arma3InstancePaths.instanceDirName(getId());
        String profileSubdirectory = SystemUtils.getOsType() == SystemUtils.OSType.WINDOWS ? "Users" : "home";
        return paths.getInstanceProfilesPath(getId())
                .resolve(profileSubdirectory)
                .resolve(serverProfile)
                .resolve(serverProfile + ".Arma3Profile")
                .toFile();
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
