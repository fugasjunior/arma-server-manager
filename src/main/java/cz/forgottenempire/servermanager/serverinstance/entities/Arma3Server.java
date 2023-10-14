package cz.forgottenempire.servermanager.serverinstance.entities;

import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.workshop.Arma3CDLC;
import cz.forgottenempire.servermanager.workshop.WorkshopMod;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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

    @Override
    public List<String> getLaunchParameters() {
        List<String> parameters = new ArrayList<>();
        parameters.add("-port=" + getPort());
        parameters.add("-config=" + getConfigFileForServer().getAbsolutePath());
        parameters.add("-profiles=\"" + pathsFactory.getProfilesDirectoryPath().toAbsolutePath() + "\"");
        parameters.add("-name=" + ServerType.ARMA3 + "_" + getId());
        parameters.add("-nosplash");
        parameters.add("-skipIntro");
        parameters.add("-world=empty");
        addModsAndDlcsToParameters(parameters);
        return parameters;
    }

    private File getConfigFileForServer() {
        String fileName = "ARMA3_" + getId() + ".cfg";
        return pathsFactory.getConfigFilePath(ServerType.ARMA3, fileName).toFile();
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
