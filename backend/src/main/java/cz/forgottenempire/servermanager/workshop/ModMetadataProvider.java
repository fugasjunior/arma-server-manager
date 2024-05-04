package cz.forgottenempire.servermanager.workshop;

import java.util.Optional;

public interface ModMetadataProvider {
    Optional<WorkshopFileDetailsService.ModMetadata> fetchModMetadata(long modId);
}
