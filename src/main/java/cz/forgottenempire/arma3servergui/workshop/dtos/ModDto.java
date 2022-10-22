package cz.forgottenempire.arma3servergui.workshop.dtos;

import java.util.Date;
import javax.persistence.Id;
import lombok.Data;

@Data
public class ModDto {
    @Id
    private Long id;
    private String name;
    private Long fileSize;
    private Date lastUpdated;
    private String installationStatus;
    private String errorStatus;
}
