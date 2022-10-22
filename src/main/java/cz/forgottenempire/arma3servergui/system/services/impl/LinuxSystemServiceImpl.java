package cz.forgottenempire.arma3servergui.system.services.impl;

import com.google.common.base.Supplier;
import cz.forgottenempire.arma3servergui.system.conditions.LinuxEnvironmentCondition;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
@Conditional(LinuxEnvironmentCondition.class)
@Slf4j
public class LinuxSystemServiceImpl extends AbstractSystemServiceImpl {

    @Override
    protected Supplier<Long> memoryLeftSupplier() {
        return () -> getValueFromMemInfo("MemAvailable");
    }

    @Override
    protected Supplier<Long> memoryTotalSupplier() {
        return () -> getValueFromMemInfo("MemMotal");
    }

    @Override
    protected Supplier<Double> cpuUsageSupplier() {
        return () -> osBean.getSystemLoadAverage() / osBean.getAvailableProcessors();
    }

    private Long getValueFromMemInfo(String key) {
        try {
            Process process = new ProcessBuilder("/bin/sh", "-c",
                    "cat /proc/meminfo | grep " + key + " | sed 's/[^0-9]//g'")
                    .start();
            BufferedReader is = new BufferedReader(new InputStreamReader(process.getInputStream()));
            process.waitFor();
            return Long.parseLong(is.readLine()) * 1024;
        } catch (IOException e) {
            log.error("Could not get free memory", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted", e);
        }
        return 0L;
    }
}