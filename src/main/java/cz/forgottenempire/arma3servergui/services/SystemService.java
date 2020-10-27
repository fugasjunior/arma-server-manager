package cz.forgottenempire.arma3servergui.services;

public interface SystemService {

    long getDiskSpaceLeft();

    long getDiskSpaceTotal();

    long getMemoryLeft();

    long getMemoryTotal();

    double getCpuUsage();
}
