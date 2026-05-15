package cz.forgottenempire.servermanager.support.dsl;

import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.serverinstance.dtos.Arma3DifficultySettingsDto;
import cz.forgottenempire.servermanager.serverinstance.dtos.Arma3ServerDto;
import cz.forgottenempire.servermanager.modpreset.dtos.CreatePresetRequestDto;

import java.util.List;

public final class Builders {

    private Builders() {}

    public static Arma3ServerDto arma3Server(String name, int port) {
        Arma3ServerDto dto = new Arma3ServerDto();
        dto.setType(ServerType.ARMA3);
        dto.setName(name);
        dto.setPort(port);
        dto.setQueryPort(port + 1);
        dto.setMaxPlayers(20);
        dto.setActiveMods(List.of());
        dto.setActiveDLCs(List.of());
        dto.setCustomLaunchParameters(List.of());
        dto.setDifficultySettings(new Arma3DifficultySettingsDto());
        return dto;
    }

    public static CreatePresetRequestDto preset(String name, ServerType type, List<Long> modIds) {
        CreatePresetRequestDto dto = new CreatePresetRequestDto();
        dto.setName(name);
        dto.setType(type);
        dto.setMods(modIds);
        return dto;
    }
}
