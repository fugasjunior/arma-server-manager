package cz.forgottenempire.arma3servergui.workshop;

import cz.forgottenempire.arma3servergui.common.exceptions.NotFoundException;
import java.util.List;

public enum Arma3CDLC {
    CSLA_IRON_CURTAIN("CSLA Iron Curtain", "csla"),
    GLOBAL_MOBILIZATION("Global Mobilization", "gm"),
    SOG_PRAIRIE_FIRE("S.O.G. Prairie Fire", "vn"),
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
        return List.of(CSLA_IRON_CURTAIN, GLOBAL_MOBILIZATION, SOG_PRAIRIE_FIRE, WESTERN_SAHARA);
    }

    public static Arma3CDLC fromId(String id) {
        return switch (id) {
            case "csla" -> CSLA_IRON_CURTAIN;
            case "gm" -> GLOBAL_MOBILIZATION;
            case "vn" -> SOG_PRAIRIE_FIRE;
            case "ws" -> WESTERN_SAHARA;
            default -> throw new NotFoundException("Unexpected CDLC ID: " + id);
        };
    }
}
