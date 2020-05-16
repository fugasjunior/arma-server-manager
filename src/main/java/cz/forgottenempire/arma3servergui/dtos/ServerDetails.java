package cz.forgottenempire.arma3servergui.dtos;

import lombok.Data;

@Data
public class ServerDetails {
    private String version;
    private boolean updating;

    private long spaceLeft;
    private long spaceTotal;
}
