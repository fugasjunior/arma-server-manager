package cz.forgottenempire.arma3servergui.server.services.impl;

import cz.forgottenempire.arma3servergui.Constants;
import cz.forgottenempire.arma3servergui.model.ServerSettings;
import cz.forgottenempire.arma3servergui.server.repositories.ServerSettingsRepository;
import cz.forgottenempire.arma3servergui.server.services.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettingsServiceImpl implements SettingsService {

    private ServerSettingsRepository settingsRepository;

    @Override
    public ServerSettings getServerSettings() {
        return settingsRepository.findAll().stream().findFirst().orElse(new ServerSettings());
    }

    @Override
    public void setServerSettings(ServerSettings settings) {
        settingsRepository.save(settings);
    }

    @Autowired
    public void setSettingsRepository(ServerSettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }
}
