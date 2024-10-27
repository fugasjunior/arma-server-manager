package cz.forgottenempire.servermanager.workshop.metadata;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.forgottenempire.servermanager.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@Slf4j
class WorkshopApiMetadataProvider {

    private static final String REQUEST_URL = Constants.STEAM_API_URL + "?key=%s&itemcount=1&publishedfileids[0]=%d";

    private final String steamApiKey;
    private final RestTemplate restTemplate;

    @Autowired
    WorkshopApiMetadataProvider(@Value("${steam.api.key}") String steamApiKey, RestTemplate restTemplate) {
        this.steamApiKey = steamApiKey;
        this.restTemplate = restTemplate;
    }

    Optional<ModMetadata> fetchModMetadata(long modId) {
        JsonPropertyProvider propertyProvider = createPropertyProvider(modId);
        if (propertyProvider == null) {
            return Optional.empty();
        }

        String modName = propertyProvider.findName();
        String consumerAppId = propertyProvider.findConsumerAppId();
        if (modName == null || consumerAppId == null) {
            return Optional.empty();
        }

        return Optional.of(new ModMetadata(modName, consumerAppId));
    }

    private JsonPropertyProvider createPropertyProvider(long modId) {
        JsonNode modInfoJson = getModInfoFromSteamApi(modId);
        if (modInfoJson == null) {
            return null;
        }
        return new JsonPropertyProvider(modInfoJson);
    }

    private JsonNode getModInfoFromSteamApi(long modId) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(prepareRequest(modId), String.class);
            JsonNode parsedResponse = new ObjectMapper().readTree(response.getBody()).findValue("response");
            return parsedResponse.findValue("publishedfiledetails").get(0);
        } catch (RestClientException e) {
            log.error("Request to Steam Workshop API for mod ID '{}' failed", modId, e);
            return null;
        } catch (JsonProcessingException e) {
            log.error("Failed to process Workshop API response for mod ID {}", modId, e);
            return null;
        }
    }

    private String prepareRequest(long modId) {
        return REQUEST_URL.formatted(steamApiKey, modId);
    }
}
