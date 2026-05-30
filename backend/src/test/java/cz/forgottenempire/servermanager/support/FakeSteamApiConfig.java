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

    @Autowired
    private RestTemplate restTemplate;

    @PostConstruct
    void installFakeSteamApiInterceptor() {
        restTemplate.getInterceptors().add((request, body, execution) -> {
            String url = request.getURI().toString();
            String json = buildBatchResponse(url);
            return new MockClientHttpResponse(json.getBytes(StandardCharsets.UTF_8), HttpStatus.OK);
        });
    }

    private String buildBatchResponse(String url) {
        // Brackets may be percent-encoded (%5B/%5D) or literal depending on RestTemplate URI handling.
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("publishedfileids[^=&]+=(\\d+)")
                .matcher(url);
        StringBuilder entries = new StringBuilder();
        while (m.find()) {
            String modId = m.group(1);
            String[] meta = resolveModMeta(modId);
            if (!entries.isEmpty()) entries.append(",");
            entries.append("{\"publishedfileid\":\"%s\",\"title\":\"%s\",\"consumer_appid\":\"%s\"}"
                    .formatted(modId, meta[0], meta[1]));
        }
        return "{\"response\":{\"publishedfiledetails\":[" + entries + "]}}";
    }

    // consumer_appid 107410 = ARMA3, 221100 = DAYZ
    private String[] resolveModMeta(String modId) {
        return switch (modId) {
            case "450814997" -> new String[]{"CBA_A3", "107410"};
            case "463939057" -> new String[]{"ace", "107410"};
            case "2872023707" -> new String[]{"SilentZ Particles", "221100"};
            default -> new String[]{"Unknown Mod", "107410"};
        };
    }
}
