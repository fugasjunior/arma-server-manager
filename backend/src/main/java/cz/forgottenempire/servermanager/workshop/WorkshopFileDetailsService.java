package cz.forgottenempire.servermanager.workshop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import cz.forgottenempire.servermanager.common.Constants;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class WorkshopFileDetailsService {

    private final String steamApiKey;

    private final LoadingCache<Long, JsonNode> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(6, TimeUnit.HOURS)
            .build(new CacheLoader<>() {
                @Override
                public JsonNode load(@NonNull Long key) {
                    return getModInfo(key);
                }
            });
    private final RestTemplate restTemplate;

    @Autowired
    public WorkshopFileDetailsService(@Value("${steam.api.key}") String steamApiKey, RestTemplate restTemplate) {
        this.steamApiKey = steamApiKey;
        this.restTemplate = restTemplate;
    }

    public Optional<ModMetadata> fetchModMetadata(long modId) {
        String modName = getValueFromInfo(modId, "title");
        String consumerAppIdString = getValueFromInfo(modId, "consumer_app_id");
        Long consumerAppId = consumerAppIdString != null ?
                Long.parseLong(consumerAppIdString)
                : null;

        if (modName == null || consumerAppId == null) {
            return Optional.empty();
        }

        return Optional.of(new ModMetadata(modName, consumerAppId));
    }

    public String getModName(Long modId) {
        return getValueFromInfo(modId, "title");
    }

    public Long getModAppId(Long modId) {
        String value = getValueFromInfo(modId, "consumer_app_id");
        if (value == null) {
            return null;
        }

        Long appId = null;
        try {
            appId = Long.parseLong(value);
        } catch (NumberFormatException ignored) {
        }

        return appId;
    }

    private String getValueFromInfo(Long modId, String key) {
        JsonNode modInfo = loadModInfoFromCache(modId);
        if (modInfo == null) {
            return null;
        }

        JsonNode titleNode = modInfo.findValue(key);
        return titleNode == null ? null : titleNode.asText();
    }

    private JsonNode getModInfo(Long modId) {
        log.debug("Getting info for mod {} from Steam Workshop", modId);

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

    private JsonNode loadModInfoFromCache(Long modId) {
        // the mod info is cached to avoid unnecessary calls to the Workshop API
        try {
            return cache.get(modId);
        } catch (ExecutionException e) {
            log.error("Could not get mod info for mod id {}", modId, e);
            return null;
        }
    }

    record ModMetadata(@Nonnull String name, long consumerAppId) {
    }
}
