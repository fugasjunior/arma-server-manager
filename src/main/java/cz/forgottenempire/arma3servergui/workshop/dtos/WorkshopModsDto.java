package cz.forgottenempire.arma3servergui.workshop.dtos;

import java.util.Date;
import java.util.List;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkshopModsDto {
    private List<WorkshopModDto> workshopMods;
}
