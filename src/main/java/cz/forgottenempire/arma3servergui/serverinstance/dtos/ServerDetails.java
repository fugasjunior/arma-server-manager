package cz.forgottenempire.arma3servergui.serverinstance.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServerDetails {

    private long spaceLeft;
    private long spaceTotal;

    private long memoryLeft;
    private long memoryTotal;

    private double cpuUsage;
    private int cpuCount;

    private String osName;
    private String osVersion;
    private String osArchitecture;
}