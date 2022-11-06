package cz.forgottenempire.servermanager.workshop;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
class ModsDto {

    private List<ModDto> workshopMods;
    private List<CreatorDlcDto> creatorDlcs;
}
