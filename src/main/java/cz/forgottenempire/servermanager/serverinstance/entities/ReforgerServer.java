package cz.forgottenempire.servermanager.serverinstance.entities;

import cz.forgottenempire.servermanager.common.Constants;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.serverinstance.ServerConfig;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotEmpty;
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
public class ReforgerServer extends Server {

    @NotEmpty
    private String scenarioId;

    private boolean thirdPersonViewEnabled;
    private boolean battlEye;

    @ElementCollection
    private List<ReforgerMod> activeMods;

    @Override
    public List<String> getLaunchParameters() {
        List<String> parameters = new ArrayList<>();
        parameters.add("-config");
        parameters.add(getConfigFile().getAbsolutePath());
        parameters.add("-maxFPS");
        parameters.add("60");
        parameters.add("-backendlog");
        parameters.add("-logAppend");
        addCustomLaunchParameters(parameters);
        return parameters;
    }

    @Override
    public Collection<ServerConfig> getConfigFiles() {
        return List.of(new ServerConfig(getConfigFile(), Constants.SERVER_CONFIG_TEMPLATES.get(ServerType.REFORGER), this));
    }

    private File getConfigFile() {
        String fileName = "REFORGER_" + getId() + ".json";
        return pathsFactory.getConfigFilePath(ServerType.REFORGER, fileName).toFile();
    }

    private void addCustomLaunchParameters(List<String> parameters) {
        getCustomLaunchParameters().forEach(parameter -> {
            parameters.add("-" + parameter.getName());
            if (parameter.getValue() != null) {
                parameters.add(parameter.getValue());
            }
        });
    }
}