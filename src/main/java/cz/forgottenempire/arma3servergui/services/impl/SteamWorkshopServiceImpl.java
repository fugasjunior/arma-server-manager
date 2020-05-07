package cz.forgottenempire.arma3servergui.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.forgottenempire.arma3servergui.services.SteamWorkshopService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class SteamWorkshopServiceImpl implements SteamWorkshopService {
    @Value("${steam.api.key}")
    private String steamApiKey;

    private final String STEAM_API_URL = "https://api.steampowered.com/ISteamRemoteStorage/GetPublishedFileDetails/v1/";

    private final Logger logger = LoggerFactory.getLogger(SteamWorkshopServiceImpl.class);

    @Override
    public String getModName(Long modId) {
        Optional<JsonNode> modInfo = getModInfo(modId);
        if(modInfo.isEmpty()) return null;

        JsonNode titleNode = modInfo.get().findValue("title");
        return titleNode == null ? null : titleNode.asText();
    }

    @Override
    public String getModDescription(Long modId) {
        Optional<JsonNode> modInfo = getModInfo(modId);
        if(modInfo.isEmpty()) return null;

        JsonNode titleNode = modInfo.get().findValue("description");
        return titleNode == null ? null : titleNode.asText();
    }

    private Optional<JsonNode> getModInfo(Long modId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
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
            logger.warn("Could not load info for mod id: " + modId);
        }

        return modInfo == null ? Optional.empty() : Optional.of(modInfo);
    }
}
