package cz.forgottenempire.arma3servergui.system;

import cz.forgottenempire.arma3servergui.util.SystemUtils;
import cz.forgottenempire.arma3servergui.util.SystemUtils.OSType;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.lang.NonNull;

public class LinuxEnvironmentCondition implements Condition {

    public boolean matches(ConditionContext context, @NonNull AnnotatedTypeMetadata metadata) {
        return SystemUtils.getOsType() == OSType.LINUX;
    }
}
