package cz.forgottenempire.arma3servergui.serverinstance.dtos;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServersDto {

    private List<ServerDto> servers;
}
