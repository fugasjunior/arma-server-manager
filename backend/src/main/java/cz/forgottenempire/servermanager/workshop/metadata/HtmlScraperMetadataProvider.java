package cz.forgottenempire.servermanager.workshop.metadata;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

@Service
@Slf4j
public class HtmlScraperMetadataProvider implements ModMetadataProvider {

    private static final String WORKSHOP_PAGE_URL_BASE = "https://steamcommunity.com/sharedfiles/filedetails/?id=";
    private static final String MOD_NAME_SELECTOR = ".workshopItemTitle";
    private static final String CONSUMER_APP_ID_ATTRIBUTE_KEY = "data-appid";
    private static final String CONSUMER_APP_ID_SELECTOR = "[" + CONSUMER_APP_ID_ATTRIBUTE_KEY + "]";

    private final HttpClient httpClient;

    @Autowired
    public HtmlScraperMetadataProvider(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public Optional<ModMetadata> fetchModMetadata(long modId) {
        Document document = fetchWorkshopPageHtml(modId);
        if (document == null) {
            return Optional.empty();
        }

        String modName = findModName(document);
        String consumerAppId = findConsumerAppId(document);
        if (modName == null || consumerAppId == null) {
            return Optional.empty();
        }

        return Optional.of(new ModMetadata(
                modName,
                consumerAppId)
        );
    }

    private Document fetchWorkshopPageHtml(long modId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(WORKSHOP_PAGE_URL_BASE + modId))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return Jsoup.parse(response.body());
        } catch (IOException | InterruptedException e) {
            log.error("Failed to fetch mod metadata from workshop page", e);
            return null;
        }
    }

    private static String findModName(Document document) {
        Element modNameElement = document.selectFirst(MOD_NAME_SELECTOR);
        return modNameElement == null ? null : modNameElement.text();
    }

    private static String findConsumerAppId(Document document) {
        Element consumerAppIdElement = document.selectFirst(CONSUMER_APP_ID_SELECTOR);
        return consumerAppIdElement == null ? null : consumerAppIdElement.attr(CONSUMER_APP_ID_ATTRIBUTE_KEY);
    }
}
