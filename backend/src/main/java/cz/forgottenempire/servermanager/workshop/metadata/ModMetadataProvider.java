package cz.forgottenempire.servermanager.workshop.metadata;

import java.util.Optional;

public interface ModMetadataProvider {
    Optional<ModMetadata> fetchModMetadata(long modId);
}
