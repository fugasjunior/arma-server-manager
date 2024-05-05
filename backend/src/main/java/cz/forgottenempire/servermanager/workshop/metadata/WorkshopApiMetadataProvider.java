package cz.forgottenempire.servermanager.workshop.metadata;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@Slf4j
public class WorkshopApiMetadataProvider implements ModMetadataProvider {
    private final String steamApiKey;
    private final RestTemplate restTemplate;

    @Autowired
    public WorkshopApiMetadataProvider(@Value("${steam.api.key}") String steamApiKey, RestTemplate restTemplate) {
        this.steamApiKey = steamApiKey;
        this.restTemplate = restTemplate;
    }

    @Override
    public Optional<ModMetadata> fetchModMetadata(long modId) {
        JsonNode modInfoJson = getModInfoFromSteamApi(modId);
        if (modInfoJson == null) {
            return Optional.empty();
        }

        String modName = findModName(modInfoJson);
        String consumerAppId = findConsumerAppId(modInfoJson);
        if (modName == null || consumerAppId == null) {
            return Optional.empty();
        }

        return Optional.of(new ModMetadata(modName, consumerAppId));
    }

    private JsonNode getModInfoFromSteamApi(Long modId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("key", steamApiKey);
        map.add("itemcount", "1");
        map.add("publishedfileids[0]", modId.toString());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<String> test = restTemplate.postForEntity(Constants.STEAM_API_URL, request, String.class);

        JsonNode modInfo = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            modInfo = objectMapper.readTree(test.getBody()).findValue("publishedfiledetails");
        } catch (JsonProcessingException e) {
            log.warn("Could not load info for mod id: " + modId);
        }

        return modInfo;
    }

    private String findModName(JsonNode modInfoJson) {
        return getValueFromJson("title", modInfoJson);
    }

    private String findConsumerAppId(JsonNode modInfoJson) {
        return getValueFromJson("consumer_app_id", modInfoJson);
    }

    private String getValueFromJson(String key, JsonNode modInfoJson) {
        JsonNode value = modInfoJson.findValue(key);
        return value != null ? value.asText() : null;
    }
}
