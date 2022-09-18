package cz.forgottenempire.arma3servergui.server.services;

import cz.forgottenempire.arma3servergui.server.entities.Server;

public interface SettingsService {

    Server getServerSettings();

    void setServerSettings(Server settings);
}
