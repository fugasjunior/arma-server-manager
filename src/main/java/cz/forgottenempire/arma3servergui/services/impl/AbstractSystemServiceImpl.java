package cz.forgottenempire.arma3servergui.services.impl;

import com.sun.management.OperatingSystemMXBean;
import cz.forgottenempire.arma3servergui.services.SystemService;
import java.io.File;
import java.lang.management.ManagementFactory;
import org.springframework.beans.factory.annotation.Value;

public abstract class AbstractSystemServiceImpl implements SystemService {

    @Value("${installDir}")
    private String installDir;

    protected final OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

    @Override
    public long getDiskSpaceLeft() {
        return new File(installDir).getUsableSpace();
    }

    @Override
    public long getDiskSpaceTotal() {
        return new File(installDir).getTotalSpace();
    }
}
