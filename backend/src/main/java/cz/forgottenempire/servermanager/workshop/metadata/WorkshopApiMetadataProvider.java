package cz.forgottenempire.servermanager.workshop.metadata;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.forgottenempire.servermanager.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
class WorkshopApiMetadataProvider extends AbstractModMetadataProvider {
    private final String steamApiKey;
    private final RestTemplate restTemplate;

    @Autowired
    WorkshopApiMetadataProvider(@Value("${steam.api.key}") String steamApiKey, RestTemplate restTemplate) {
        this.steamApiKey = steamApiKey;
        this.restTemplate = restTemplate;
    }

    @Override
    PropertyProvider createPropertyProvider(long modId) {
        JsonNode modInfoJson = getModInfoFromSteamApi(modId);
        if (modInfoJson == null) {
            return null;
        }
        return new JsonPropertyProvider(modInfoJson);
    }

    private JsonNode getModInfoFromSteamApi(long modId) {
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(Constants.STEAM_API_URL, prepareRequest(modId), String.class);
            JsonNode parsedResponse = new ObjectMapper().readTree(response.getBody());
            return parsedResponse.findValue("publishedfiledetails");
        } catch (RestClientException e) {
            log.error("Request to Steam Workshop API for mod ID '{}' failed", modId, e);
            return null;
        } catch (JsonProcessingException e) {
            log.error("Failed to process Workshop API response for mod ID '{}'", modId, e);
            return null;
        }
    }

    private HttpEntity<MultiValueMap<String, String>> prepareRequest(long modId) {
        return new HttpEntity<>(prepareRequestBody(modId), prepareRequestHeaders());
    }

    private MultiValueMap<String, String> prepareRequestBody(long modId) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("key", steamApiKey);
        map.add("itemcount", "1");
        map.add("publishedfileids[0]", String.valueOf(modId));
        return map;
    }

    private static HttpHeaders prepareRequestHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return headers;
    }
}
