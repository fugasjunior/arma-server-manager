package cz.forgottenempire.arma3servergui.workshop;

import cz.forgottenempire.arma3servergui.common.ServerType;
import javax.persistence.Id;
import lombok.Data;

@Data
public class ModDto {

    @Id
    private Long id;
    private String name;
    private ServerType serverType;
    private Long fileSize;
    private String lastUpdated;
    private String installationStatus;
    private String errorStatus;
}
