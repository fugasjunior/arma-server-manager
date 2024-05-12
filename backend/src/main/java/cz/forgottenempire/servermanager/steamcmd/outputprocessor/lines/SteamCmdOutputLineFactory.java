package cz.forgottenempire.servermanager.steamcmd.outputprocessor.lines;

import cz.forgottenempire.servermanager.common.Constants;
import cz.forgottenempire.servermanager.steamcmd.SteamCmdJob;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class SteamCmdOutputLineFactory {

    private static final String UPDATE_STATE_REGEX_PREFIX = "update state \\(0x\\d+\\) ";
    private final static Pattern UPDATE_STATE_DOWNLOADING = Pattern.compile(UPDATE_STATE_REGEX_PREFIX + "downloading");
    private final static Pattern UPDATE_STATE_VERIFYING = Pattern.compile(UPDATE_STATE_REGEX_PREFIX + "verifying");
    private final static Pattern UPDATE_STATE_PREALLOCATING = Pattern.compile(UPDATE_STATE_REGEX_PREFIX + "preallocating");
    private final static Pattern UPDATE_STATE_COMMITTING = Pattern.compile(UPDATE_STATE_REGEX_PREFIX + "committing");

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
        } else if (UPDATE_STATE_PREALLOCATING.matcher(normalizedLine).find()) {
            lineObject = new AppUpdatePreallocatingLine(normalizedLine, Constants.SERVER_IDS.get(job.getRelatedServer()));
        } else if (UPDATE_STATE_COMMITTING.matcher(normalizedLine).find()) {
            lineObject = new AppUpdateCommittingLine(normalizedLine, Constants.SERVER_IDS.get(job.getRelatedServer()));
        } else {
            lineObject = null;
        }
        return lineObject;
    }
}
