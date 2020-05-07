package cz.forgottenempire.arma3servergui.services;

public interface SteamWorkshopService {
    String getModName(Long modId);
    String getModDescription(Long modId);
}
