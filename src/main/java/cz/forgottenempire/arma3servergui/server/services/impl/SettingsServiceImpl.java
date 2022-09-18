package cz.forgottenempire.arma3servergui.server.services.impl;

import cz.forgottenempire.arma3servergui.Constants;
import cz.forgottenempire.arma3servergui.server.entities.Server;
import cz.forgottenempire.arma3servergui.server.repositories.ServerRepository;
import cz.forgottenempire.arma3servergui.server.services.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettingsServiceImpl implements SettingsService {

    private ServerRepository settingsRepository;

    @Override
    public Server getServerSettings() {
        return settingsRepository.findById(Constants.SERVER_MAIN_ID).orElse(new Server());
    }

    @Override
    public void setServerSettings(Server settings) {
        settings.setId(Constants.SERVER_MAIN_ID);
        settingsRepository.save(settings);
    }

    @Autowired
    public void setSettingsRepository(ServerRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }
}
