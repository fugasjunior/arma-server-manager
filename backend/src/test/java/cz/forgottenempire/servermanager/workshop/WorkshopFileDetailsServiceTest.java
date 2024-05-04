package cz.forgottenempire.servermanager.workshop;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkshopFileDetailsServiceTest {

    @Mock(stubOnly = true)
    private RestTemplate restTemplate;
    @Mock(stubOnly = true)
    private ResponseEntity<String> response;

    private WorkshopFileDetailsService fileDetailsService;

    @BeforeEach
    void setUp() {
        fileDetailsService = new WorkshopFileDetailsService(restTemplate);
    }

    @Test
    void whenGettingModNameOfExistingPublicMod_thenModNameReturned() {
        when(response .getBody()).thenReturn(
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

    @Test
    void whenGettingModNameOfNonExistingMod_thenNullReturned() {
        when(response .getBody()).thenReturn(
                """
                        {
                          "response": {
                              "publishedfiledetails": []
                          }
                        }
                        """);
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class))).thenReturn(response);

        String modName = fileDetailsService.getModName(1234L);

        assertThat(modName).isNull();
    }
}