package cz.forgottenempire.arma3servergui.system.services.impl;

import com.google.common.base.Supplier;
import cz.forgottenempire.arma3servergui.system.conditions.WindowsEnvironmentCondition;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

@Service
@Conditional(WindowsEnvironmentCondition.class)
public class WindowsSystemServiceImpl extends AbstractSystemServiceImpl {

    @Override
    protected Supplier<Long> memoryLeftSupplier() {
        return osBean::getFreeMemorySize;
    }

    @Override
    protected Supplier<Long> memoryTotalSupplier() {
        return osBean::getTotalMemorySize;
    }

    @Override
    protected Supplier<Double> cpuUsageSupplier() {
        return osBean::getCpuLoad;
    }
}