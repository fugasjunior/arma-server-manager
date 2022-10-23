package cz.forgottenempire.arma3servergui.workshop.dtos;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModsDto {
    private List<ModDto> workshopMods;
    private List<CreatorDlcDto> creatorDlcs;
}
