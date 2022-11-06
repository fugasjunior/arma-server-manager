package cz.forgottenempire.servermanager.system;

import cz.forgottenempire.servermanager.util.SystemUtils;
import cz.forgottenempire.servermanager.util.SystemUtils.OSType;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.lang.NonNull;

class LinuxEnvironmentCondition implements Condition {

    public boolean matches(@NonNull ConditionContext context, @NonNull AnnotatedTypeMetadata metadata) {
        return SystemUtils.getOsType() == OSType.LINUX;
    }
}
