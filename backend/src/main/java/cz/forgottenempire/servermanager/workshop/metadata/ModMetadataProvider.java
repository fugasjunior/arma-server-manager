package cz.forgottenempire.servermanager.workshop.metadata;

import java.util.Optional;

public interface ModMetadataProvider {
    Optional<ModMetadataService.ModMetadata> fetchModMetadata(long modId);
}
