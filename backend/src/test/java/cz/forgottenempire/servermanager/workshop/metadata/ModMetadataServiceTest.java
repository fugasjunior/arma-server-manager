package cz.forgottenempire.servermanager.workshop.metadata;

import cz.forgottenempire.servermanager.common.Constants;
import cz.forgottenempire.servermanager.common.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModMetadataServiceTest {

    private static final String WORKSHOP_PAGE_URL_BASE = "https://steamcommunity.com/sharedfiles/filedetails/?id=";
    private static final String STEAM_API_KEY = "ABCD1234";
    private static final long MOD_ID = 1L;
    private static final long UNLISTED_MOD_ID = 2L;
    private static final long NON_EXISTING_MOD_ID = 3L;

    @Mock(stubOnly = true)
    private RestTemplate restTemplate;
    @Mock(stubOnly = true)
    private ResponseEntity<String> restResponse;
    @Mock(stubOnly = true)
    private HttpResponse<String> htmlResponse;
    @Mock(stubOnly = true)
    private HttpClient httpClient;

    private ModMetadataService fileDetailsService;

    @BeforeEach
    void setUp() {
        WorkshopApiMetadataProvider workshopApiMetadataProvider = new WorkshopApiMetadataProvider(STEAM_API_KEY, restTemplate);
        HtmlScraperMetadataProvider htmlScraperMetadataProvider = new HtmlScraperMetadataProvider(httpClient);
        fileDetailsService = new ModMetadataService(workshopApiMetadataProvider, htmlScraperMetadataProvider);
    }

    @Test
    void whenFetchingModMetadataForExistingPublicMod_thenDataAreFetchedFromSteamApi() {
        when(restTemplate.postForEntity(Constants.STEAM_API_URL, prepareRestRequest(MOD_ID), String.class))
                .thenReturn(restResponse);
        when(restResponse.getBody()).thenReturn(
                """
                        {
                          "response": {
                            "publishedfiledetails": [
                              {
                                "title": "Mod Name",
                                "consumer_app_id": "107410"
                              }
                            ]
                          }
                        }
                        """);

        ModMetadata metadata = fileDetailsService.fetchModMetadata(MOD_ID);

        assertThat(metadata.name()).isEqualTo("Mod Name");
        assertThat(metadata.consumerAppId()).isEqualTo("107410");
    }

    @Test
    void whenFetchingModMetadataForExistingUnlistedMod_thenDataAreScrapedFromModPageHtml() throws Exception {
        when(restTemplate.postForEntity(Constants.STEAM_API_URL, prepareRestRequest(UNLISTED_MOD_ID), String.class))
                .thenReturn(restResponse);
        when(restResponse.getBody()).thenReturn(
                """
                        {
                          "response": {
                            "publishedfiledetails": []
                          }
                        }
                        """);
        when(httpClient.send(prepareHttpRequest(UNLISTED_MOD_ID), HttpResponse.BodyHandlers.ofString()))
                .thenReturn(htmlResponse);
        when(htmlResponse.body()).thenReturn("""
                <html>
                    <head>
                        <title>Steam Workshop::Mod Name</title>
                    </head>
                    <body>
                        <a data-appid="107410">
                            <span>Store Page</span>
                        </a>
                        <div class="workshopItemTitle">Mod Name</div>
                    </body>
                </html>
                """);

        ModMetadata metadata = fileDetailsService.fetchModMetadata(UNLISTED_MOD_ID);

        assertThat(metadata.name()).isEqualTo("Mod Name");
        assertThat(metadata.consumerAppId()).isEqualTo("107410");
    }

    @Test
    void whenFetchingModMetadataForNonExistingMod_thenNotFoundExceptionIsThrown() throws Exception {
        when(restTemplate.postForEntity(Constants.STEAM_API_URL, prepareRestRequest(NON_EXISTING_MOD_ID), String.class))
                .thenReturn(restResponse);
        when(restResponse.getBody()).thenReturn(
                """
                        {
                          "response": {
                            "publishedfiledetails": []
                          }
                        }
                        """);
        when(httpClient.send(prepareHttpRequest(NON_EXISTING_MOD_ID), HttpResponse.BodyHandlers.ofString()))
                .thenReturn(htmlResponse);
        when(htmlResponse.body()).thenReturn("""
                <html>
                    <head>
                        <title>Steam Community :: Error</title>
                    </head>
                    <body>
                        <h2>Error</h2>
                    </body>
                </html>
                """);

        assertThatThrownBy(() -> fileDetailsService.fetchModMetadata(NON_EXISTING_MOD_ID))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Mod ID " + NON_EXISTING_MOD_ID + " not found.");
    }


    @Test
    void whenFetchingFromSteamApiFails_thenHtmlScraperIsUsedInstead() throws Exception {
        when(restTemplate.postForEntity(Constants.STEAM_API_URL, prepareRestRequest(MOD_ID), String.class))
                .thenThrow(new RestClientException("REST call failed."));
        when(httpClient.send(prepareHttpRequest(MOD_ID), HttpResponse.BodyHandlers.ofString()))
                .thenReturn(htmlResponse);
        when(htmlResponse.body()).thenReturn("""
                <html>
                    <head>
                        <title>Steam Workshop::Mod Name</title>
                    </head>
                    <body>
                        <a data-appid="107410">
                            <span>Store Page</span>
                        </a>
                        <div class="workshopItemTitle">Mod Name</div>
                    </body>
                </html>
                """);

        ModMetadata metadata = fileDetailsService.fetchModMetadata(MOD_ID);

        assertThat(metadata.name()).isEqualTo("Mod Name");
        assertThat(metadata.consumerAppId()).isEqualTo("107410");
    }

    private static HttpEntity<MultiValueMap<String, String>> prepareRestRequest(long modId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("key", STEAM_API_KEY);
        map.add("itemcount", "1");
        map.add("publishedfileids[0]", String.valueOf(modId));

        return new HttpEntity<>(map, headers);
    }

    private static HttpRequest prepareHttpRequest(long modId) {
        return HttpRequest.newBuilder()
                .uri(URI.create(WORKSHOP_PAGE_URL_BASE + modId))
                .build();
    }
}
