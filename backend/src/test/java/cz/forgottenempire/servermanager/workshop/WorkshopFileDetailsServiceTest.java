package cz.forgottenempire.servermanager.workshop;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class WorkshopFileDetailsServiceTest {

    @Test
    void whenGettingModNameOfExistingPublicMod_thenModNameReturned() {
        RestTemplate restTemplate = mock(RestTemplate.class);
        WorkshopFileDetailsService fileDetailsService = new WorkshopFileDetailsService(restTemplate);

        @SuppressWarnings("unchecked")
        ResponseEntity<String> response = (ResponseEntity<String>) mock(ResponseEntity.class, withSettings().stubOnly());
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
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class))).thenReturn(response);

        String modName = fileDetailsService.getModName(1234L);

        assertThat(modName).isEqualTo("Mod Name");
    }
}