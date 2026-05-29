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

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModMetadataServiceTest {

    private static final String STEAM_API_KEY = "ABCD1234";
    private static final long MOD_ID = 1L;
    private static final long MOD_ID_2 = 2L;

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
        when(restTemplate.getForEntity(buildUrl(List.of(MOD_ID)), String.class)).thenReturn(restResponse);
        when(restResponse.getBody()).thenReturn(singleModResponse(MOD_ID, "Mod Name", "107410"));

        ModMetadata metadata = fileDetailsService.fetchModMetadata(MOD_ID);

        assertThat(metadata.name()).isEqualTo("Mod Name");
        assertThat(metadata.consumerAppId()).isEqualTo("107410");
    }

    @Test
    void whenFetchingModMetadataForNonExistingMod_thenNotFoundExceptionIsThrown() {
        when(restTemplate.getForEntity(buildUrl(List.of(MOD_ID)), String.class)).thenReturn(restResponse);
        when(restResponse.getBody()).thenReturn(emptyResponse());

        assertThatThrownBy(() -> fileDetailsService.fetchModMetadata(MOD_ID))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Mod ID " + MOD_ID + " not found.");
    }

    @Test
    void whenBatchFetchingMetadataForMultipleMods_thenAllFoundModsAreReturned() {
        when(restTemplate.getForEntity(buildUrl(List.of(MOD_ID, MOD_ID_2)), String.class)).thenReturn(restResponse);
        when(restResponse.getBody()).thenReturn(twoModResponse());

        Map<Long, ModMetadata> result = fileDetailsService.fetchModMetadata(List.of(MOD_ID, MOD_ID_2));

        assertThat(result).hasSize(2);
        assertThat(result.get(MOD_ID).name()).isEqualTo("Mod One");
        assertThat(result.get(MOD_ID).consumerAppId()).isEqualTo("107410");
        assertThat(result.get(MOD_ID_2).name()).isEqualTo("Mod Two");
        assertThat(result.get(MOD_ID_2).consumerAppId()).isEqualTo("221100");
    }

    @Test
    void whenBatchFetchingMetadataAndSomeModsAreMissing_thenMissingModsAbsentFromResult() {
        when(restTemplate.getForEntity(buildUrl(List.of(MOD_ID, MOD_ID_2)), String.class)).thenReturn(restResponse);
        when(restResponse.getBody()).thenReturn(singleModResponse(MOD_ID, "Mod One", "107410"));

        Map<Long, ModMetadata> result = fileDetailsService.fetchModMetadata(List.of(MOD_ID, MOD_ID_2));

        assertThat(result).hasSize(1);
        assertThat(result).containsKey(MOD_ID);
        assertThat(result).doesNotContainKey(MOD_ID_2);
    }

    @Test
    void whenBatchFetchingMetadataAndAllModsAreMissing_thenEmptyMapReturnedWithoutException() {
        when(restTemplate.getForEntity(buildUrl(List.of(MOD_ID)), String.class)).thenReturn(restResponse);
        when(restResponse.getBody()).thenReturn(emptyResponse());

        Map<Long, ModMetadata> result = fileDetailsService.fetchModMetadata(List.of(MOD_ID));

        assertThat(result).isEmpty();
    }

    private static String buildUrl(List<Long> ids) {
        StringBuilder sb = new StringBuilder(Constants.STEAM_API_URL);
        sb.append("?key=").append(STEAM_API_KEY);
        sb.append("&itemcount=").append(ids.size());
        for (int i = 0; i < ids.size(); i++) {
            sb.append("&publishedfileids[").append(i).append("]=").append(ids.get(i));
        }
        return sb.toString();
    }

    private static String singleModResponse(long modId, String name, String appId) {
        return """
                {
                  "response": {
                    "publishedfiledetails": [
                      {
                        "publishedfileid": "%d",
                        "title": "%s",
                        "consumer_appid": "%s"
                      }
                    ]
                  }
                }
                """.formatted(modId, name, appId);
    }

    private static String twoModResponse() {
        return """
                {
                  "response": {
                    "publishedfiledetails": [
                      {
                        "publishedfileid": "1",
                        "title": "Mod One",
                        "consumer_appid": "107410"
                      },
                      {
                        "publishedfileid": "2",
                        "title": "Mod Two",
                        "consumer_appid": "221100"
                      }
                    ]
                  }
                }
                """;
    }

    private static String emptyResponse() {
        return """
                {
                  "response": {
                    "publishedfiledetails": []
                  }
                }
                """;
    }
}
