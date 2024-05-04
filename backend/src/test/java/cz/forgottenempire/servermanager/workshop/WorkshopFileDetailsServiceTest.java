package cz.forgottenempire.servermanager.workshop;

import cz.forgottenempire.servermanager.common.Constants;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkshopFileDetailsServiceTest {

    private static final String STEAM_API_KEY = "ABCD1234";
    private static final long NON_EXISTING_MOD_ID = 1L;
    private static final long MOD_ID = 2L;

    @Mock(stubOnly = true)
    private RestTemplate restTemplate;
    @Mock(stubOnly = true)
    private ResponseEntity<String> response;

    private WorkshopFileDetailsService fileDetailsService;

    @BeforeEach
    void setUp() {
        fileDetailsService = new WorkshopFileDetailsService(STEAM_API_KEY, restTemplate);
    }

    @Test
    void whenGettingModNameOfExistingPublicMod_thenModNameReturned() {
        when(restTemplate.postForEntity(Constants.STEAM_API_URL, prepareRequest(MOD_ID), String.class))
                .thenReturn(response);
        when(response.getBody()).thenReturn(
                """
                        {
                          "response": {
                            "publishedfiledetails": [
                              {
                                "title": "Mod Name"
                              }
                            ]
                          }
                        }
                        """);

        String modName = fileDetailsService.getModName(MOD_ID);

        assertThat(modName).isEqualTo("Mod Name");
    }

    @Test
    void whenGettingModNameOfNonExistingMod_thenNullReturned() {
        when(restTemplate.postForEntity(Constants.STEAM_API_URL, prepareRequest(NON_EXISTING_MOD_ID), String.class))
                .thenReturn(response);
        when(response.getBody()).thenReturn(
                """
                        {
                          "response": {
                              "publishedfiledetails": []
                          }
                        }
                        """);

        String modName = fileDetailsService.getModName(NON_EXISTING_MOD_ID);

        assertThat(modName).isNull();
    }

    @Test
    void whenGettingAppIdOfExistingPublicMod_thenAppIdReturned() {
        when(restTemplate.postForEntity(Constants.STEAM_API_URL, prepareRequest(MOD_ID), String.class))
                .thenReturn(response);
        when(response.getBody()).thenReturn(
                """
                        {
                          "response": {
                            "publishedfiledetails": [
                              {
                                "consumer_app_id": "107410"
                              }
                            ]
                          }
                        }
                        """);

        Long appId = fileDetailsService.getModAppId(MOD_ID);

        assertThat(appId).isEqualTo(107410L);
    }

    @Test
    void whenGettingAppIdOfNonExistingMod_thenNullReturned() {
        when(restTemplate.postForEntity(Constants.STEAM_API_URL, prepareRequest(NON_EXISTING_MOD_ID), String.class))
                .thenReturn(response);
        when(response.getBody()).thenReturn(
                """
                        {
                          "response": {
                              "publishedfiledetails": []
                          }
                        }
                        """);

        Long appId = fileDetailsService.getModAppId(NON_EXISTING_MOD_ID);

        assertThat(appId).isNull();
    }

    @Test
    void whenFetchingModMetadataForExistingPublicMod_thenDataAreFetchedFromSteamApi() {
        when(restTemplate.postForEntity(Constants.STEAM_API_URL, prepareRequest(MOD_ID), String.class))
                .thenReturn(response);
        when(response.getBody()).thenReturn(
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

        Optional<WorkshopFileDetailsService.ModMetadata> metadata = fileDetailsService.fetchModMetadata(MOD_ID);

        assertThat(metadata).isPresent();
        assertThat(metadata.get().name()).isEqualTo("Mod Name");
        assertThat(metadata.get().consumerAppId()).isEqualTo(107410L);
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
