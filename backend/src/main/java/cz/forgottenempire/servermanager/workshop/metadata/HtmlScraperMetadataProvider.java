package cz.forgottenempire.servermanager.workshop.metadata;

import lombok.extern.slf4j.Slf4j;
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

    private final HttpClient httpClient;

    @Autowired
    public HtmlScraperMetadataProvider(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public Optional<ModMetadataService.ModMetadata> fetchModMetadata(long modId) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://steamcommunity.com/sharedfiles/filedetails/?id=" + modId))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String html = response.body();
            System.out.println("HTML Content:\n" + html);
        } catch (IOException | InterruptedException e) {
            log.error("Failed to do stuff"); // TODO log message
        }

        return Optional.empty();
    }
}
