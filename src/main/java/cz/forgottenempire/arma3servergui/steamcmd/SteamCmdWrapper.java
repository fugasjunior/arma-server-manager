package cz.forgottenempire.arma3servergui.steamcmd;

import cz.forgottenempire.arma3servergui.steamcmd.exceptions.IOOperationException;
import cz.forgottenempire.arma3servergui.steamcmd.exceptions.LoginException;
import cz.forgottenempire.arma3servergui.steamcmd.exceptions.NoMatchException;
import cz.forgottenempire.arma3servergui.steamcmd.exceptions.NoSubscriptionException;
import cz.forgottenempire.arma3servergui.workshop.entities.DownloadStatus;
import cz.forgottenempire.arma3servergui.workshop.entities.ErrorStatus;
import java.io.File;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SteamCmdWrapper {

    @Value("${steamcmd.path}")
    private String steamCmdPath;

    public DownloadStatus execute(SteamCmdParameters parameters) {
        SteamCmdExecutor executor = new SteamCmdExecutor(new File(steamCmdPath), parameters);
        try {
            executor.execute();
        } catch (LoginException e) {
            log.error("Login to SteamCmd failed", e);
            return new DownloadStatus(ErrorStatus.WRONG_AUTH, e);
        } catch (NoSubscriptionException e) {
            log.error("No Steam subcription to selected item", e);
            return new DownloadStatus(ErrorStatus.NO_SUBSCRIPTION, e);
        } catch (IOOperationException e) {
            log.error("SteamCmd failed during IO operation", e);
            return new DownloadStatus(ErrorStatus.IO, e);
        } catch (NoMatchException e) {
            log.error("Steam item not found", e);
            return new DownloadStatus(ErrorStatus.NO_MATCH, e);
        } catch (Exception e) {
            log.error("SteamCmd execution failed", e);
            return new DownloadStatus(ErrorStatus.GENERIC, e);
        }
        return new DownloadStatus();
    }
}