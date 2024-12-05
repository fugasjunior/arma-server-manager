package cz.forgottenempire.servermanager.workshop.metadata;

import cz.forgottenempire.servermanager.common.Constants;
import cz.forgottenempire.servermanager.common.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModMetadataServiceTest {

    private static final String STEAM_API_KEY = "ABCD1234";
    private static final long MOD_ID = 1L;
    private static final String REQUEST_URL = Constants.STEAM_API_URL + "?key=" + STEAM_API_KEY + "&itemcount=1&publishedfileids[0]=" + MOD_ID;

    @Mock(stubOnly = true)
    private RestTemplate restTemplate;
    @Mock(stubOnly = true)
    private ResponseEntity<String> restResponse;

    private ModMetadataService fileDetailsService;

    @BeforeEach
    void setUp() {
        WorkshopApiMetadataProvider workshopApiMetadataProvider = new WorkshopApiMetadataProvider(STEAM_API_KEY, restTemplate);
        fileDetailsService = new ModMetadataService(workshopApiMetadataProvider);
    }

    @Test
    void whenFetchingModMetadataForExistingMod_thenDataAreFetchedFromSteamApi() {
        when(restTemplate.getForEntity(REQUEST_URL, String.class))
                .thenReturn(restResponse);
        when(restResponse.getBody()).thenReturn(
                """
                        {
                          "response": {
                            "publishedfiledetails": [
                              {
                                "title": "Mod Name",
                                "consumer_appid": "107410"
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
    void whenFetchingModMetadataForNonExistingMod_thenNotFoundExceptionIsThrown() {
        when(restTemplate.getForEntity(REQUEST_URL, String.class))
                .thenReturn(restResponse);
        when(restResponse.getBody()).thenReturn(
                """
                        {
                          "response": {
                            "publishedfiledetails": []
                          }
                        }
                        """);

        assertThatThrownBy(() -> fileDetailsService.fetchModMetadata(MOD_ID))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Mod ID " + MOD_ID + " not found.");
    }
}
