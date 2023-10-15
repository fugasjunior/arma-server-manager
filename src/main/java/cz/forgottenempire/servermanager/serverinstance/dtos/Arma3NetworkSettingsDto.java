package cz.forgottenempire.servermanager.serverinstance.dtos;

import lombok.Data;

@Data
public class Arma3NetworkSettingsDto {
    private Integer maxMessagesSend;
    private Integer maxSizeGuaranteed;
    private Integer maxSizeNonguaranteed;
    private Integer minBandwidth;
    private Integer maxBandwidth;
    private Double minErrorToSend;
    private Double minErrorToSendNear;
    private Integer maxPacketSize;
    private Integer maxCustomFileSize;
}
