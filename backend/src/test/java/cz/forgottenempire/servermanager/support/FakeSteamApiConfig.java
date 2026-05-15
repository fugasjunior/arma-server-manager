package cz.forgottenempire.servermanager.support;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

@TestConfiguration
public class FakeSteamApiConfig {

    // Minimal Steam Workshop API response; title and consumer_appid are read by JsonPropertyProvider.
    // consumer_appid 107410 = ARMA3, 221100 = DAYZ
    private static final String STEAM_RESPONSE_TEMPLATE =
            "{\"response\":{\"publishedfiledetails\":[{\"title\":\"%s\",\"consumer_appid\":\"%s\"}]}}";

    @Autowired
    private RestTemplate restTemplate;

    @PostConstruct
    void installFakeSteamApiInterceptor() {
        restTemplate.getInterceptors().add((request, body, execution) -> {
            String url = request.getURI().toString();
            String[] meta = resolveModMeta(url);
            String json = STEAM_RESPONSE_TEMPLATE.formatted(meta[0], meta[1]);
            return new MockClientHttpResponse(json.getBytes(StandardCharsets.UTF_8), HttpStatus.OK);
        });
    }

    private String[] resolveModMeta(String url) {
        if (url.contains("450814997")) return new String[]{"CBA_A3", "107410"};
        if (url.contains("463939057")) return new String[]{"ace", "107410"};
        if (url.contains("2872023707")) return new String[]{"SilentZ Particles", "221100"};
        return new String[]{"Unknown Mod", "107410"};
    }
}
