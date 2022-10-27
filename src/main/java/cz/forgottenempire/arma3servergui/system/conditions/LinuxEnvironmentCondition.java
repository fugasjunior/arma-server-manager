package cz.forgottenempire.arma3servergui.system.conditions;

import cz.forgottenempire.arma3servergui.common.util.SystemUtils;
import cz.forgottenempire.arma3servergui.common.util.SystemUtils.OSType;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.lang.NonNull;

public class LinuxEnvironmentCondition implements Condition {

    public boolean matches(ConditionContext context, @NonNull AnnotatedTypeMetadata metadata) {
        return SystemUtils.getOsType() == OSType.LINUX;
    }
}
