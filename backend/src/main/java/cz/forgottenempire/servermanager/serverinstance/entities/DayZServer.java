package cz.forgottenempire.servermanager.serverinstance.entities;

import cz.forgottenempire.servermanager.common.Constants;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.serverinstance.ServerConfig;
import cz.forgottenempire.servermanager.workshop.WorkshopMod;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
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

    @ManyToMany(fetch = FetchType.EAGER)
    private List<WorkshopMod> activeMods;

    @Override
    public List<String> getLaunchParameters() {
        List<String> parameters = new ArrayList<>();
        parameters.add("-port=" + getPort());
        parameters.add("-config=" + getConfigFile().getAbsolutePath());
        parameters.add("-limitFPS=60");
        parameters.add("-dologs");
        parameters.add("-adminlog");
        parameters.add("-freezeCheck");
        addModsToParameters(parameters);
        addCustomLaunchParameters(parameters);
        return parameters;
    }

    @Override
    public Collection<ServerConfig> getConfigFiles() {
        return List.of(new ServerConfig(getConfigFile(), Constants.SERVER_CONFIG_TEMPLATES.get(ServerType.DAYZ), this));
    }

    private File getConfigFile() {
        String fileName = "DAYZ_" + getId() + ".cfg";
        return pathsFactory.getConfigFilePath(getType(), fileName).toFile();
    }

    private void addModsToParameters(List<String> parameters) {
        if (activeMods.isEmpty()) {
            return;
        }

        addModsToParameters(getActiveServerMods(), "-serverMod=", parameters);
        addModsToParameters(getActiveClientMods(), "-mod=", parameters);
    }

    private static void addModsToParameters(List<WorkshopMod> mods, String parameterPrefix, List<String> parameters) {
        if (!mods.isEmpty()) {
            StringBuilder serverModsList = new StringBuilder(parameterPrefix);
            mods.forEach(mod -> serverModsList.append(mod.getNormalizedName()).append(";"));
            parameters.add(serverModsList.toString());
        }
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
