package cz.forgottenempire.arma3servergui.system.services.impl;

import cz.forgottenempire.arma3servergui.system.conditions.WindowsEnvironmentCondition;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

@Service
@Conditional(WindowsEnvironmentCondition.class)
public class WindowsSystemServiceImpl extends AbstractSystemServiceImpl {

    @Override
    public long getMemoryLeft() {
        return osBean.getFreePhysicalMemorySize();
    }

    @Override
    public long getMemoryTotal() {
        return osBean.getTotalPhysicalMemorySize();
    }

    @Override
    public double getCpuUsage() {
        // on Windows, the preferable getSystemLoadAverage() method always returns -1, so this is a workaround
        return osBean.getCpuLoad();
    }
}