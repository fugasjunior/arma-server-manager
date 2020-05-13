package cz.forgottenempire.arma3servergui.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import cz.forgottenempire.arma3servergui.services.SteamWorkshopService;
import lombok.extern.slf4j.Slf4j;
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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class SteamWorkshopServiceImpl implements SteamWorkshopService {

    private final String STEAM_API_URL = "https://api.steampowered.com/ISteamRemoteStorage/GetPublishedFileDetails/v1/";

    @Value("${steam.api.key}")
    private String steamApiKey;

    private final LoadingCache<Long, JsonNode> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(6, TimeUnit.HOURS)
            .build(new CacheLoader<>() {
                @Override
                public JsonNode load(@NonNull Long key) {
                    return getModInfo(key);
                }
            });

    @Override
    public String getModName(Long modId) {
        return getValueFromInfo(modId, "title");
    }

    @Override
    public String getModDescription(Long modId) {
        return getValueFromInfo(modId, "description");
    }

    @Override
    public Long getModAppId(Long modId) {
        String value = getValueFromInfo(modId, "consumer_app_id");
        if (value == null) return null;

        Long appId = null;
        try {
            appId = Long.parseLong(value);
        } catch (NumberFormatException ignored) {}

        return appId;
    }

    @Override
    public Long getFileSize(Long modId) {
        String value = getValueFromInfo(modId, "file_size");
        if (value == null) return null;

        Long size = null;
        try {
            size = Long.parseLong(value);
        } catch (NumberFormatException ignored) {}

        return size;
    }

    private String getValueFromInfo(Long modId, String key) {
        JsonNode modInfo = loadModInfoFromCache(modId);
        if (modInfo == null) return null;

        JsonNode titleNode = modInfo.findValue(key);
        return titleNode == null ? null : titleNode.asText();
    }

    private JsonNode getModInfo(Long modId) {
        log.info("Getting info for mod {} from Steam Workshop", modId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("key", steamApiKey);
        map.add("itemcount", "1");
        map.add("publishedfileids[0]", modId.toString());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> test = restTemplate.postForEntity(STEAM_API_URL, request, String.class);

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
        try {
            return cache.get(modId);
        } catch (ExecutionException e) {
            log.error("Could not get mod info for mod id {} due to {}", modId, e.toString());
            return null;
        }
    }
}
