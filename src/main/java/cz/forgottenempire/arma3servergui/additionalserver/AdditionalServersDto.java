package cz.forgottenempire.arma3servergui.additionalserver;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
class AdditionalServersDto {

    private List<AdditionalServerDto> servers;

    public AdditionalServersDto() {
        servers = new ArrayList<>();
    }

    public AdditionalServersDto(List<AdditionalServerDto> servers) {
        this.servers = servers;
    }

    public void add(AdditionalServerDto serverDto) {
        servers.add(serverDto);
    }
}
