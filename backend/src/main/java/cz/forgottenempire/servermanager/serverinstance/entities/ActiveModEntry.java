package cz.forgottenempire.servermanager.serverinstance.entities;

public interface ActiveModEntry {
    int getPosition();
    boolean isServerOnly();
    boolean isLoadOnHeadlessClient();
    String getLaunchName();
}
