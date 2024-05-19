package cz.forgottenempire.servermanager.steamcmd.outputprocessor.lines;

import cz.forgottenempire.servermanager.steamcmd.outputprocessor.SteamCmdItemInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class WorkshopItemDownloadingLine implements SteamCmdOutputLine {

    private static final Pattern ITEM_ID_PATTERN = Pattern.compile("\\d+");
    private final String line;

    public WorkshopItemDownloadingLine(String line) {
        this.line = line;
    }

    @Override
    public SteamCmdItemInfo parseInfo() {
        Matcher matcher = ITEM_ID_PATTERN.matcher(line);
        if (!matcher.find()) {
            log.error("Failed to parse item ID from line '{}'", line);
            return null;
        }

        long itemId = Long.parseLong(matcher.group());
        return new SteamCmdItemInfo(itemId, SteamCmdItemInfo.SteamCmdStatus.DOWNLOADING, 0, 0, 0);
    }
}
