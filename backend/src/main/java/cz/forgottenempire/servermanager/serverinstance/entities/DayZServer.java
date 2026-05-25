package cz.forgottenempire.servermanager.serverinstance.entities;

import cz.forgottenempire.servermanager.common.Constants;
import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.serverinstance.ServerConfig;
import cz.forgottenempire.servermanager.serverinstance.ServerLaunchContext;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "dayzserver")
public class DayZServer extends Server {

    @Min(0)
    private int respawnTime;

    private boolean persistent;
    private boolean vonEnabled;
    private boolean forceSameBuild;
    private boolean thirdPersonViewEnabled;
    private boolean crosshairEnabled;
    private boolean clientFilePatching;

    @DecimalMin("0.1")
    @DecimalMax("64")
    private double timeAcceleration;

    @DecimalMin("0.1")
    @DecimalMax("64")
    private double nightTimeAcceleration;

    @Column(columnDefinition = "LONGTEXT")
    private String additionalOptions;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "dayzserver_id", nullable = false)
    @OrderColumn(name = "mod_order")
    private List<DayZServerActiveMod> activeMods = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "dayzserver_id", nullable = false)
    @OrderColumn(name = "mod_order")
    private List<DayZServerActiveLocalMod> activeLocalMods = new ArrayList<>();

    @Override
    public List<String> getLaunchParameters(ServerLaunchContext ctx) {
        List<String> parameters = new ArrayList<>();
        parameters.add("-port=" + getPort());
        parameters.add("-config=" + getConfigFile(ctx.pathsFactory()).getAbsolutePath());
        parameters.add("-limitFPS=60");
        parameters.add("-dologs");
        parameters.add("-adminlog");
        parameters.add("-freezeCheck");
        addModsToParameters(parameters);
        addCustomLaunchParameters(parameters);
        return parameters;
    }

    @Override
    public Collection<ServerConfig> getConfigFiles(ServerLaunchContext ctx) {
        return List.of(new ServerConfig(getConfigFile(ctx.pathsFactory()), Constants.SERVER_CONFIG_TEMPLATES.get(ServerType.DAYZ), this, ctx.freeMarkerConfigurer()));
    }

    private File getConfigFile(PathsFactory pathsFactory) {
        String fileName = "DAYZ_" + getId() + ".cfg";
        return pathsFactory.getConfigFilePath(getType(), fileName).toFile();
    }

    private void addModsToParameters(List<String> parameters) {
        List<ActiveModEntry> ordered = orderedActiveMods().toList();
        addCombinedModParam(
                ordered.stream().filter(ActiveModEntry::isServerOnly).map(ActiveModEntry::getLaunchName).toList(),
                "-serverMod=", parameters);
        addCombinedModParam(
                ordered.stream().filter(e -> !e.isServerOnly()).map(ActiveModEntry::getLaunchName).toList(),
                "-mod=", parameters);
    }

    private static void addCombinedModParam(List<String> names, String prefix, List<String> parameters) {
        if (!names.isEmpty()) {
            StringBuilder sb = new StringBuilder(prefix);
            names.forEach(name -> sb.append(name).append(";"));
            parameters.add(sb.toString());
        }
    }

    private Stream<ActiveModEntry> orderedActiveMods() {
        return Stream.concat(
                activeMods.stream().map(e -> (ActiveModEntry) e),
                activeLocalMods.stream().map(e -> (ActiveModEntry) e)
        ).sorted(Comparator.comparingInt(ActiveModEntry::getPosition));
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
