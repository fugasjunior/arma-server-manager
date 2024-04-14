package cz.forgottenempire.servermanager.serverinstance.dtos;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import cz.forgottenempire.servermanager.common.ServerType;

import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type",
        visible = true)
@JsonSubTypes({
        @Type(value = Arma3ServerDto.class, name = "ARMA3"),
        @Type(value = DayZServerDto.class, name = "DAYZ"),
        @Type(value = DayZServerDto.class, name = "DAYZ_EXP"),
        @Type(value = ReforgerServerDto.class, name = "REFORGER")
})
public interface ServerDto {

    Long getId();

    void setId(Long id);

    ServerType getType();

    List<LaunchParameterDto> getCustomLaunchParameters();

    void setCustomLaunchParameters(List<LaunchParameterDto> customLaunchParameters);

    AutomaticRestartDto getAutomaticRestart();

    void setAutomaticRestart(AutomaticRestartDto automaticRestartDto);
}
