package cz.forgottenempire.servermanager.workshop.metadata;

import cz.forgottenempire.servermanager.common.exceptions.NotFoundException;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class ModMetadataService {

    private final WorkshopApiMetadataProvider apiMetadataProvider;
    private final HtmlScraperMetadataProvider htmlScraperMetadataProvider;

    @Autowired
    public ModMetadataService(
            WorkshopApiMetadataProvider apiMetadataProvider,
            HtmlScraperMetadataProvider htmlScraperMetadataProvider
    ) {
        this.apiMetadataProvider = apiMetadataProvider;
        this.htmlScraperMetadataProvider = htmlScraperMetadataProvider;
    }

    public ModMetadata fetchModMetadata(long modId) {
        return apiMetadataProvider.fetchModMetadata(modId)
                .orElseGet(() -> htmlScraperMetadataProvider.fetchModMetadata(modId)
                        .orElseThrow(() -> new NotFoundException("Mod ID " + modId + " not found.")));
    }

    public record ModMetadata(@Nonnull String name, @Nonnull String consumerAppId) {
    }
}
