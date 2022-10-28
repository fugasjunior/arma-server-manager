package cz.forgottenempire.arma3servergui.system;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.sun.management.OperatingSystemMXBean;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;

public abstract class AbstractSystemServiceImpl implements SystemService {

    protected final OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

    protected final Supplier<Long> diskSpaceLeftCache = Suppliers.memoizeWithExpiration(diskSpaceLeftSupplier(),
            5, TimeUnit.SECONDS);
    protected final Supplier<Long> diskSpaceTotalCache = Suppliers.memoizeWithExpiration(diskSpaceTotalSupplier(),
            1, TimeUnit.DAYS);
    protected final Supplier<Long> memoryLeftCache = Suppliers.memoizeWithExpiration(memoryLeftSupplier(),
            5, TimeUnit.SECONDS);
    protected final Supplier<Long> memoryTotalCache = Suppliers.memoizeWithExpiration(memoryTotalSupplier(),
            365, TimeUnit.DAYS);
    protected final Supplier<Double> cpuUsageCache = Suppliers.memoizeWithExpiration(cpuUsageSupplier(),
            5, TimeUnit.SECONDS);

    protected final Supplier<Integer> processorCountCache = Suppliers.memoizeWithExpiration(processorCountSupplier(),
            365, TimeUnit.DAYS);

    protected final Supplier<String> osNameCache = Suppliers.memoizeWithExpiration(osNameSupplier(),
            365, TimeUnit.DAYS);
    protected final Supplier<String> osVersionCache = Suppliers.memoizeWithExpiration(osVersionSupplier(),
            365, TimeUnit.DAYS);
    protected final Supplier<String> osArchitectureCache = Suppliers.memoizeWithExpiration(osArchitectureSupplier(),
            365, TimeUnit.DAYS);

    @Override
    public final long getDiskSpaceLeft() {
        return diskSpaceLeftCache.get();
    }

    @Override
    public final long getDiskSpaceTotal() {
        return diskSpaceTotalCache.get();
    }

    @Override
    public final long getMemoryLeft() {
        return memoryLeftCache.get();
    }

    @Override
    public final long getMemoryTotal() {
        return memoryTotalCache.get();
    }

    @Override
    public final double getCpuUsage() {
        return cpuUsageCache.get();
    }

    @Override
    public int getProcessorCount() {
        return processorCountCache.get();
    }

    @Override
    public String getOsName() {
        return osNameCache.get();
    }

    @Override
    public String getOsVersion() {
        return osVersionCache.get();
    }

    @Override
    public String getOsArchitecture() {
        return osArchitectureCache.get();
    }

    protected Supplier<Long> diskSpaceLeftSupplier() {
        // TODO this can be incorrect for system with multiple drives, preferably use the same drive as the game servers
        return () -> new File("/").getUsableSpace();
    }

    protected Supplier<Long> diskSpaceTotalSupplier() {
        // TODO this can be incorrect for system with multiple drives, preferably use the same drive as the game servers
        return () -> new File("/").getTotalSpace();
    }

    protected Supplier<Integer> processorCountSupplier() {
        return osBean::getAvailableProcessors;
    }

    protected Supplier<String> osNameSupplier() {
        return osBean::getName;
    }

    protected Supplier<String> osVersionSupplier() {
        return osBean::getVersion;
    }

    protected Supplier<String> osArchitectureSupplier() {
        return osBean::getArch;
    }

    protected abstract Supplier<Long> memoryLeftSupplier();

    protected abstract Supplier<Long> memoryTotalSupplier();

    protected abstract Supplier<Double> cpuUsageSupplier();
}
