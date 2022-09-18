package cz.forgottenempire.arma3servergui.server.dtos;

import cz.forgottenempire.arma3servergui.creatorDLC.entities.CreatorDLC;
import java.util.List;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ServerWorkshopModDto {
    private Long id;
    private String name;
}
