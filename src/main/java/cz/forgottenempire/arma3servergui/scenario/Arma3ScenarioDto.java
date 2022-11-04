package cz.forgottenempire.arma3servergui.scenario;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
class Arma3ScenarioDto {

    private String name;
    private Long fileSize;
    private LocalDateTime createdOn;
}
