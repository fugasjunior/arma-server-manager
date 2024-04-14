package cz.forgottenempire.servermanager.system;

interface SystemService {

    long getDiskSpaceLeft();

    long getDiskSpaceTotal();

    long getMemoryLeft();

    long getMemoryTotal();

    double getCpuUsage();

    int getProcessorCount();

    String getOsName();

    String getOsVersion();

    String getOsArchitecture();
}
