package cz.forgottenempire.servermanager.serverinstance.entities;

public interface ActiveModEntry {
    int getPosition();
    boolean isServerOnly();
    String getLaunchName();
}
