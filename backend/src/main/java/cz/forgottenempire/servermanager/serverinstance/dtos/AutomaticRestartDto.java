package cz.forgottenempire.servermanager.serverinstance.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalTime;

@Data
@AllArgsConstructor
public class AutomaticRestartDto {
    private boolean enabled;
    private LocalTime time;
}
