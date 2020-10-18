package cz.forgottenempire.arma3servergui.services;

import cz.forgottenempire.arma3servergui.model.ServerSettings;

public interface SettingsService {

    ServerSettings getServerSettings();

    void setServerSettings(ServerSettings settings);
}
