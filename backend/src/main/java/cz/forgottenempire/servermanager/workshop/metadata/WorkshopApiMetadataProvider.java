package cz.forgottenempire.servermanager.workshop.metadata;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import cz.forgottenempire.servermanager.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
class WorkshopApiMetadataProvider {

    private static final String STEAM_API_KEY_PARAM = "?key=%s";
    private static final String ITEM_COUNT_PARAM = "&itemcount=%d";
    private static final String PUBLISHED_FILE_ID_PARAM = "&publishedfileids[%d]=%d";

    private final String steamApiKey;
    private final RestTemplate restTemplate;

    @Autowired
    WorkshopApiMetadataProvider(@Value("${steam.api.key}") String steamApiKey, RestTemplate restTemplate) {
        this.steamApiKey = steamApiKey;
        this.restTemplate = restTemplate;
    }

    Map<Long, ModMetadata> fetchModMetadata(Collection<Long> modIds) {
        if (modIds.isEmpty()) {
            return Collections.emptyMap();
        }

        String url = buildBatchRequestUrl(modIds);
        JsonNode details = getPublishedFileDetails(url);
        if (details == null) {
            return Collections.emptyMap();
        }

        Map<Long, ModMetadata> result = new HashMap<>();
        for (JsonNode entry : details) {
            JsonPropertyProvider provider = new JsonPropertyProvider(entry);
            String rawId = provider.findPublishedFileId();
            String name = provider.findName();
            String consumerAppId = provider.findConsumerAppId();
            if (rawId == null || name == null || consumerAppId == null) {
                continue;
            }
            try {
                result.put(Long.parseLong(rawId), new ModMetadata(name, consumerAppId));
            } catch (NumberFormatException e) {
                log.warn("Unexpected publishedfileid value '{}' in Steam API response", rawId);
            }
        }
        return result;
    }

    Optional<ModMetadata> fetchModMetadata(long modId) {
        return Optional.ofNullable(fetchModMetadata(java.util.List.of(modId)).get(modId));
    }

    private String buildBatchRequestUrl(Collection<Long> modIds) {
        StringBuilder sb = new StringBuilder(Constants.STEAM_API_URL);
        sb.append(STEAM_API_KEY_PARAM.formatted(steamApiKey));
        sb.append(ITEM_COUNT_PARAM.formatted(modIds.size()));
        int index = 0;
        for (Long id : modIds) {
            sb.append(PUBLISHED_FILE_ID_PARAM.formatted(index++, id));
        }
        return sb.toString();
    }

    private JsonNode getPublishedFileDetails(String url) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JsonNode parsedResponse = new ObjectMapper().readTree(response.getBody()).findValue("response");
            return parsedResponse.findValue("publishedfiledetails");
        } catch (RestClientException e) {
            log.error("Request to Steam Workshop API failed", e);
            return null;
        } catch (JacksonException e) {
            log.error("Failed to process Workshop API response", e);
            return null;
        }
    }
}
