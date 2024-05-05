package cz.forgottenempire.servermanager.workshop.metadata;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@Slf4j
class HtmlScraperMetadataProvider extends AbstractModMetadataProvider {

    private static final String WORKSHOP_PAGE_URL_BASE = "https://steamcommunity.com/sharedfiles/filedetails/?id=";

    private final HttpClient httpClient;

    @Autowired
    HtmlScraperMetadataProvider(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    PropertyProvider createPropertyProvider(long modId) {
        Document document = fetchWorkshopPageHtml(modId);
        if (document == null) {
            return null;
        }
        return new HtmlPropertyProvider(document);
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
}
