package cz.forgottenempire.servermanager.serverinstance.entities;

public interface ActiveModEntry {
    int getPosition();
    boolean isLoadOnClient();
    boolean isLoadOnServer();
    boolean isLoadOnHeadlessClient();
    String getLaunchName();
}
