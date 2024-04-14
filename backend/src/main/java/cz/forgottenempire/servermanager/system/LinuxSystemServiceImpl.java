package cz.forgottenempire.servermanager.system;

import com.google.common.base.Supplier;
import cz.forgottenempire.servermanager.common.ProcessFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
@Conditional(LinuxEnvironmentCondition.class)
@Slf4j
class LinuxSystemServiceImpl extends AbstractSystemServiceImpl {

    private final ProcessFactory processFactory;

    @Autowired
    public LinuxSystemServiceImpl(ProcessFactory processFactory) {
        this.processFactory = processFactory;
    }

    @Override
    protected Supplier<Long> memoryLeftSupplier() {
        return () -> getValueFromMemInfo("MemAvailable");
    }

    @Override
    protected Supplier<Long> memoryTotalSupplier() {
        return () -> getValueFromMemInfo("MemTotal");
    }

    @Override
    protected Supplier<Double> cpuUsageSupplier() {
        return () -> osBean.getSystemLoadAverage() / osBean.getAvailableProcessors();
    }

    private Long getValueFromMemInfo(String key) {
        try {
            Process process = processFactory.startProcess(new File("/bin/sh"),
                    List.of("-c", "cat /proc/meminfo | grep " + key + " | sed 's/[^0-9]//g'"));
            process.waitFor();
            BufferedReader is = new BufferedReader(new InputStreamReader(process.getInputStream()));
            return Long.parseLong(is.readLine()) * 1024;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted", e);
        } catch (Exception e) {
            log.error("Could not get free memory", e);
        }
        return 0L;
    }
}