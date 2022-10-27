package cz.forgottenempire.arma3servergui.common;

import cz.forgottenempire.arma3servergui.server.ServerType;
import java.util.Collections;
import java.util.Map;

public class Constants {

    public static final Map<ServerType, Long> GAME_IDS = Collections.unmodifiableMap(
            Map.of(
                    ServerType.ARMA3, 107410L,
                    ServerType.DAYZ, 221100L,
                    ServerType.DAYZ_EXP, 1024020L
            )
    );

    public static final Map<ServerType, Long> SERVER_IDS = Collections.unmodifiableMap(
            Map.of(
                    ServerType.ARMA3, 233780L,
                    ServerType.DAYZ, 223350L,
                    ServerType.DAYZ_EXP, 1042420L
            )
    );

    public final static String STEAM_API_URL = "https://api.steampowered.com/ISteamRemoteStorage/GetPublishedFileDetails/v1/";

    public final static String TEMPLATE_SERVER_CONFIG_ARMA3 = "serverConfigArma3.ftl";
}
