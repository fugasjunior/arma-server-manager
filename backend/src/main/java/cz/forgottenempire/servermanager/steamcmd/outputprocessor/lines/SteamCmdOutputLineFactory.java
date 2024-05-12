package cz.forgottenempire.servermanager.steamcmd.outputprocessor.lines;

import cz.forgottenempire.servermanager.common.Constants;
import cz.forgottenempire.servermanager.steamcmd.SteamCmdJob;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class SteamCmdOutputLineFactory {

    public static Pattern UPDATE_STATE_DOWNLOADING = Pattern.compile("update state \\(0x\\d+\\) downloading");
    public static Pattern UPDATE_STATE_VERIFYING = Pattern.compile("update state \\(0x\\d+\\) verifying");

    public SteamCmdOutputLine createSteamCmdOutputLine(String normalizedLine, SteamCmdJob job) {
        SteamCmdOutputLine lineObject;
        if (normalizedLine.startsWith("success. downloaded item")) {
            lineObject = new WorkshopItemDownloadSuccessLine(normalizedLine);
        } else if (normalizedLine.startsWith("success! app")) {
            lineObject = new AppDownloadSuccessLine(normalizedLine, 0);
        } else if (UPDATE_STATE_DOWNLOADING.matcher(normalizedLine).find()) {
            lineObject = new AppUpdateProgressLine(normalizedLine, Constants.SERVER_IDS.get(job.getRelatedServer()));
        } else if (UPDATE_STATE_VERIFYING.matcher(normalizedLine).find()) {
            lineObject = new AppUpdateVerificationLine(normalizedLine, Constants.SERVER_IDS.get(job.getRelatedServer()));
        } else {
            lineObject = null;
        }
        return lineObject;
    }
}
