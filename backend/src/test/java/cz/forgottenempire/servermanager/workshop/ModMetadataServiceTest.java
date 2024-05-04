package cz.forgottenempire.servermanager.workshop;

import cz.forgottenempire.servermanager.common.Constants;
import cz.forgottenempire.servermanager.common.exceptions.NotFoundException;
import cz.forgottenempire.servermanager.workshop.metadata.HtmlScraperMetadataProvider;
import cz.forgottenempire.servermanager.workshop.metadata.ModMetadataService;
import cz.forgottenempire.servermanager.workshop.metadata.WorkshopApiMetadataProvider;
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
import org.springframework.web.client.RestTemplate;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModMetadataServiceTest {

    private static final String STEAM_API_KEY = "ABCD1234";
    private static final long MOD_ID = 1L;
    private static final long UNLISTED_MOD_ID = 2L;
    private static final long NON_EXISTING_MOD_ID = 1L;

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
        when(restTemplate.postForEntity(Constants.STEAM_API_URL, prepareRequest(MOD_ID), String.class))
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

        ModMetadataService.ModMetadata metadata = fileDetailsService.fetchModMetadata(MOD_ID);

        assertThat(metadata.name()).isEqualTo("Mod Name");
        assertThat(metadata.consumerAppId()).isEqualTo("107410");
    }

    @Test
    void whenFetchingModMetadataForExistingUnlistedMod_thenDataAreScrapedFromModPageHtml() throws Exception {
        when(restTemplate.postForEntity(Constants.STEAM_API_URL, prepareRequest(UNLISTED_MOD_ID), String.class))
                .thenReturn(restResponse);
        when(restResponse.getBody()).thenReturn(
                """
                        {
                          "response": {
                            "publishedfiledetails": []
                          }
                        }
                        """);
        when(httpClient.send(any(), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(htmlResponse); // TODO proper parameters
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

        ModMetadataService.ModMetadata metadata = fileDetailsService.fetchModMetadata(UNLISTED_MOD_ID);

        assertThat(metadata.name()).isEqualTo("Mod Name");
        assertThat(metadata.consumerAppId()).isEqualTo("107410");
    }

    @Test
    void whenFetchingModMetadataForNonExistingMod_thenNotFoundExceptionIsThrown() throws Exception {
        when(restTemplate.postForEntity(Constants.STEAM_API_URL, prepareRequest(NON_EXISTING_MOD_ID), String.class))
                .thenReturn(restResponse);
        when(restResponse.getBody()).thenReturn(
                """
                        {
                          "response": {
                            "publishedfiledetails": []
                          }
                        }
                        """);
        when(httpClient.send(any(), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(htmlResponse); // TODO proper parameters
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

    private static HttpEntity<MultiValueMap<String, String>> prepareRequest(long modId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("key", STEAM_API_KEY);
        map.add("itemcount", "1");
        map.add("publishedfileids[0]", String.valueOf(modId));

        return new HttpEntity<>(map, headers);
    }
}
