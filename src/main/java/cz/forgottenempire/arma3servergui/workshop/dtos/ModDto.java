package cz.forgottenempire.arma3servergui.workshop.dtos;

import cz.forgottenempire.arma3servergui.server.ServerType;
import java.util.Date;
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
