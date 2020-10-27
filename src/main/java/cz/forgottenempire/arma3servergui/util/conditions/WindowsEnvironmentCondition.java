package cz.forgottenempire.arma3servergui.util.conditions;

import com.mongodb.lang.NonNull;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class WindowsEnvironmentCondition implements Condition {

    public boolean matches(ConditionContext context, @NonNull AnnotatedTypeMetadata metadata) {
        String osName = context.getEnvironment().getProperty("os.name");
        return osName != null && osName.contains("Win");
    }
}
