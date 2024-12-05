package cz.forgottenempire.servermanager.workshop.metadata;

import cz.forgottenempire.servermanager.common.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
