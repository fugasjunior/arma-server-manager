package cz.forgottenempire.arma3servergui.model;

import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@Document(collection = "serverSettings", schemaVersion = "1.0")
public class ServerSettings {
    @Id
    @NotNull
    private Long id;
    @NotEmpty
    private String name;
    @Min(1)
    private int port;
    @Min(1)
    private int maxPlayers;

    private String password;
    private String adminPassword;

    private List<Long> adminSteamIds = new ArrayList<>();

    private Set<WorkshopMod> mods = new HashSet<>();

    private List<String> messageOfTheDay = new ArrayList<>();

    private boolean clientFilePatching;
    private boolean serverFilePatching;

    private boolean persistent;

    private boolean battlEye;
    private boolean von;
    private boolean verifySignatures;

    private String additionalOptions;

}
