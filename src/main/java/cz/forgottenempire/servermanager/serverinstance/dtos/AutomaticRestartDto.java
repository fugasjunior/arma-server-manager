package cz.forgottenempire.servermanager.serverinstance.dtos;

import lombok.Data;

import java.time.LocalTime;

@Data
public class AutomaticRestartDto {
    private boolean enabled;
    private LocalTime time;
}
