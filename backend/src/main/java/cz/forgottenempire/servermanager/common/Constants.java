package cz.forgottenempire.servermanager.common;

import java.util.Map;

public class Constants {

    public static final Map<ServerType, Long> GAME_IDS = Map.of(
            ServerType.ARMA3, 107410L,
            ServerType.DAYZ, 221100L,
            ServerType.DAYZ_EXP, 1024020L,
            ServerType.REFORGER, 1874900L
    );

    public static final Map<ServerType, Long> SERVER_IDS = Map.of(
            ServerType.ARMA3, 233780L,
            ServerType.DAYZ, 223350L,
            ServerType.DAYZ_EXP, 1042420L,
            ServerType.REFORGER, 1874900L
    );

    public static final Map<ServerType, String> SERVER_EXECUTABLES = Map.of(
            ServerType.ARMA3, "arma3server_x64",
            ServerType.DAYZ, "DayZServer_x64",
            ServerType.DAYZ_EXP, "DayZServer_x64",
            ServerType.REFORGER, "ArmaReforgerServer"
    );

    public static final Map<ServerType, String> SERVER_CONFIG_TEMPLATES = Map.of(
            ServerType.ARMA3, "serverConfigArma3.ftl",
            ServerType.DAYZ, "serverConfigDayZ.ftl",
            ServerType.DAYZ_EXP, "serverConfigDayZ.ftl",
            ServerType.REFORGER, "serverConfigReforger.ftl"
    );

    public static final String ARMA3_PROFILE_TEMPLATE = "arma3ServerProfile.ftl";
    public static final String ARMA3_NETWORK_SETTINGS = "arma3NetworkSettings.ftl";

    public final static String STEAM_API_URL = "https://api.steampowered.com/IPublishedFileService/GetDetails/v1/";
}
