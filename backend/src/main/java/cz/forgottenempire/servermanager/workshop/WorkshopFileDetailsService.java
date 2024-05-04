package cz.forgottenempire.servermanager.workshop;

import cz.forgottenempire.servermanager.common.exceptions.NotFoundException;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WorkshopFileDetailsService {

    private final WorkshopApiMetadataProvider steamWorkshopFileDetailsApiService;

    @Autowired
    public WorkshopFileDetailsService(WorkshopApiMetadataProvider steamWorkshopFileDetailsApiService) {
        this.steamWorkshopFileDetailsApiService = steamWorkshopFileDetailsApiService;
    }

    public ModMetadata fetchModMetadata(long modId) {
        return steamWorkshopFileDetailsApiService.fetchModMetadata(modId)
                .orElseThrow(() -> new NotFoundException("Mod ID " + modId + " not found."));
    }

    record ModMetadata(@Nonnull String name, @Nonnull String consumerAppId) {
    }
}
