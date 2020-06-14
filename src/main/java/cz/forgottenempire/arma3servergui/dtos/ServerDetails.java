package cz.forgottenempire.arma3servergui.dtos;

import lombok.Data;

@Data
public class ServerDetails {
    private String version;
    private boolean updating;

    private String hostName;
    private int port;

    private long spaceLeft;
    private long spaceTotal;

    private long memoryLeft;
    private long memoryTotal;

    private double cpuUsage;
}
