package cz.forgottenempire.servermanager.serverinstance.dtos;

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
