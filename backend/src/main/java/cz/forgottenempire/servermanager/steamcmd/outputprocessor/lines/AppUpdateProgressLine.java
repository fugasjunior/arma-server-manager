package cz.forgottenempire.servermanager.steamcmd.outputprocessor.lines;

import cz.forgottenempire.servermanager.steamcmd.outputprocessor.SteamCmdItemInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cz.forgottenempire.servermanager.steamcmd.outputprocessor.SteamCmdItemInfo.*;

@Slf4j
public class AppUpdateProgressLine implements SteamCmdOutputLine {

    private static final Pattern BYTES_PATTERN = Pattern.compile("\\((\\d+)\\s/\\s(\\d+)\\)");
    private final String lowerCaseLine;
    private final long appId;

    public AppUpdateProgressLine(String lowerCaseLine, long appId) {
        this.lowerCaseLine = lowerCaseLine;
        this.appId = appId;
    }

    @Override
    public SteamCmdItemInfo parseInfo() {
        SteamCmdItemInfo itemInfo = null;
        try {
            Matcher matcher = BYTES_PATTERN.matcher(lowerCaseLine);
            matcher.find();
            long bytesFinished = Long.parseLong(matcher.group(1));
            long bytesTotal = Long.parseLong(matcher.group(2));
            itemInfo = new SteamCmdItemInfo(appId, SteamCmdStatus.DOWNLOADING, (double) bytesFinished / bytesTotal, bytesFinished, bytesTotal);
        } catch (NumberFormatException e) {
            log.error("Failed to parse line");
        }

        return itemInfo;
    }
}
