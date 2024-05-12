package cz.forgottenempire.servermanager.steamcmd.outputprocessor.lines;

import cz.forgottenempire.servermanager.common.Constants;
import cz.forgottenempire.servermanager.steamcmd.SteamCmdJob;
import org.springframework.stereotype.Component;

@Component
public class SteamCmdOutputLineFactory {

    public SteamCmdOutputLine createSteamCmdOutputLine(String normalizedLine, SteamCmdJob job) {
        SteamCmdOutputLine lineObject;
        if (normalizedLine.startsWith("success. downloaded item")) {
            lineObject = new WorkshopItemDownloadSuccessLine(normalizedLine);
        } else if (normalizedLine.startsWith("success! app")) {
            lineObject = new AppDownloadSuccessLine(normalizedLine, 0);
        } else if (normalizedLine.startsWith("update state (0x61) downloading")) {
            lineObject = new AppUpdateProgressLine(normalizedLine, Constants.GAME_IDS.get(job.getRelatedServer()));
        } else if (normalizedLine.startsWith("update state (0x81) verifying update")) {
            lineObject = new AppUpdateVerificationLine(normalizedLine, Constants.GAME_IDS.get(job.getRelatedServer()));
        } else {
            lineObject = null;
        }
        return lineObject;
    }
}
