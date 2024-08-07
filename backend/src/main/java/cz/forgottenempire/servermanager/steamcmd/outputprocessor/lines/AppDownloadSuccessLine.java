package cz.forgottenempire.servermanager.steamcmd.outputprocessor.lines;

import cz.forgottenempire.servermanager.steamcmd.outputprocessor.SteamCmdItemInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class AppDownloadSuccessLine implements SteamCmdOutputLine {
    private static final Pattern ITEM_ID_PATTERN = Pattern.compile("'(\\d+)'");
    private final String lowerCaseLine;
    private final long bytes;

    public AppDownloadSuccessLine(String lowerCaseLine, long bytes) {
        this.lowerCaseLine = lowerCaseLine;
        this.bytes = bytes;
    }

    @Override
    public SteamCmdItemInfo parseInfo() {
        SteamCmdItemInfo itemInfo = null;
        Matcher matcher = ITEM_ID_PATTERN.matcher(lowerCaseLine);
        matcher.find();
        String idString = matcher.group(1);
        if (StringUtils.isBlank(idString)) {
            log.error("Failed to parse item ID from line '{}'", lowerCaseLine);
        }

        try {
            long itemId = Long.parseLong(idString);
            itemInfo = new SteamCmdItemInfo(itemId, SteamCmdItemInfo.SteamCmdStatus.FINISHED, 100, bytes, bytes);

        } catch (NumberFormatException e) {
            log.error("Failed to parse item ID '{}' to long", idString, e);
        }

        return itemInfo;
    }
}
