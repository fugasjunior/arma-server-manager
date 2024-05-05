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
    private final HttpClient httpClient;

    @Autowired
    public HtmlScraperMetadataProvider(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public Optional<ModMetadata> fetchModMetadata(long modId) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(WORKSHOP_PAGE_URL_BASE + modId))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            Document document = Jsoup.parse(response.body());
            Element modNameElement = document.selectFirst(".workshopItemTitle");
            Element consumerAppIdElement = document.selectFirst("[data-appid]");

            if (modNameElement == null || consumerAppIdElement == null) {
                return Optional.empty();
            }

            return Optional.of(new ModMetadata(
                    modNameElement.text(),
                    consumerAppIdElement.attr("data-appid"))
            );
        } catch (IOException | InterruptedException e) {
            log.error("Failed to do stuff"); // TODO log message
        }

        return Optional.empty();
    }
}
