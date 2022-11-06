package cz.forgottenempire.servermanager.system;

import com.google.common.base.Supplier;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

@Service
@Conditional(WindowsEnvironmentCondition.class)
class WindowsSystemServiceImpl extends AbstractSystemServiceImpl {

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