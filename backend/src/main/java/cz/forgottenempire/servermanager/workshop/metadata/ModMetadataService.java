package cz.forgottenempire.servermanager.workshop.metadata;

import cz.forgottenempire.servermanager.common.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;

@Service
@Slf4j
public class ModMetadataService {

    private final WorkshopApiMetadataProvider apiMetadataProvider;

    @Autowired
    ModMetadataService(WorkshopApiMetadataProvider apiMetadataProvider) {
        this.apiMetadataProvider = apiMetadataProvider;
    }

    public ModMetadata fetchModMetadata(long modId) {
        return apiMetadataProvider.fetchModMetadata(modId)
                .orElseThrow(() -> new NotFoundException("Mod ID " + modId + " not found."));
    }

    /**
     * Batch-fetches metadata for multiple mod IDs in a single Steam API call.
     * IDs not found on Steam are simply absent from the returned map (no exception thrown).
     */
    public Map<Long, ModMetadata> fetchModMetadata(Collection<Long> modIds) {
        return apiMetadataProvider.fetchModMetadata(modIds);
    }
}
