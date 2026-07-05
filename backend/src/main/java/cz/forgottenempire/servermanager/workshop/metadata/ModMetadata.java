package cz.forgottenempire.servermanager.workshop.metadata;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.time.LocalDateTime;

public record ModMetadata(@Nonnull String name, @Nonnull String consumerAppId, @Nullable LocalDateTime timeUpdated) {
}
