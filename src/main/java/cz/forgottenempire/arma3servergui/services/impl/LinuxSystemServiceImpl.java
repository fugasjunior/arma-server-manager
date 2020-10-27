package cz.forgottenempire.arma3servergui.services.impl;

import cz.forgottenempire.arma3servergui.util.conditions.LinuxEnvironmentCondition;
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
    public long getMemoryLeft() {
        return getValueFromMemInfo("MemAvailable");
    }

    @Override
    public long getMemoryTotal() {
        return getValueFromMemInfo("MemTotal");
    }

    @Override
    public double getCpuUsage() {
        return osBean.getSystemLoadAverage() / osBean.getAvailableProcessors();
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