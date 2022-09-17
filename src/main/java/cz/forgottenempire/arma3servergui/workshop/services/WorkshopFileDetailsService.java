package cz.forgottenempire.arma3servergui.workshop.services;

public interface WorkshopFileDetailsService {
    String getModName(Long modId);

    String getModDescription(Long modId);

    Long getModAppId(Long modId);

    Long getFileSize(Long modId);
}
