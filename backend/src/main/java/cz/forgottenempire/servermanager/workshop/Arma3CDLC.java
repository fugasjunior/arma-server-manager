package cz.forgottenempire.servermanager.workshop;

import cz.forgottenempire.servermanager.common.exceptions.NotFoundException;

import java.util.List;

public enum Arma3CDLC {
    CSLA_IRON_CURTAIN("CSLA Iron Curtain", "csla"),
    EXPEDITIONARY_FORCES("Expeditionary Forces", "ef"),
    GLOBAL_MOBILIZATION("Global Mobilization", "gm"),
    REACTION_FORCES("Reaction Forces", "rf"),
    SOG_PRAIRIE_FIRE("S.O.G. Prairie Fire", "vn"),
    SPEARHEAD_1944("Spearhead 1944", "spe"),
    WESTERN_SAHARA("Western Sahara", "ws");

    private final String name;
    private final String id;

    Arma3CDLC(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public static List<Arma3CDLC> getAll() {
        return List.of(CSLA_IRON_CURTAIN, EXPEDITIONARY_FORCES, GLOBAL_MOBILIZATION, REACTION_FORCES, SOG_PRAIRIE_FIRE, SPEARHEAD_1944, WESTERN_SAHARA);
    }

    public static Arma3CDLC fromId(String id) {
        return switch (id) {
            case "csla" -> CSLA_IRON_CURTAIN;
            case "gm" -> GLOBAL_MOBILIZATION;
            case "ef" -> EXPEDITIONARY_FORCES;
            case "rf" -> REACTION_FORCES;
            case "vn" -> SOG_PRAIRIE_FIRE;
            case "spe" -> SPEARHEAD_1944;
            case "ws" -> WESTERN_SAHARA;
            default -> throw new NotFoundException("Unexpected CDLC ID: " + id);
        };
    }
}
