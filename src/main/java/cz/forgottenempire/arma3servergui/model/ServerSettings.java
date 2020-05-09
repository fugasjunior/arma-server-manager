package cz.forgottenempire.arma3servergui.model;

import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@Document(collection = "serverSettings", schemaVersion = "1.0")
public class ServerSettings {
    @Id
    private Long id;
    private String name;
    private int port;

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
