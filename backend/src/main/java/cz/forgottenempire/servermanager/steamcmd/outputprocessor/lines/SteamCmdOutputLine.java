package cz.forgottenempire.servermanager.steamcmd.outputprocessor.lines;

import cz.forgottenempire.servermanager.steamcmd.outputprocessor.SteamCmdItemInfo;

public interface SteamCmdOutputLine {
    SteamCmdItemInfo parseInfo();
}
