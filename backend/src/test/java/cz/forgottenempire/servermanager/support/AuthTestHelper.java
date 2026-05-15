package cz.forgottenempire.servermanager.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@Component
public class AuthTestHelper {

    @Value("${auth.username}")
    private String username;

    @Value("${auth.password}")
    private String password;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String cachedToken;

    public String getToken() {
        if (cachedToken == null) {
            try {
                MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/login")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                                .param("username", username)
                                .param("password", password)
                ).andReturn();

                String body = result.getResponse().getContentAsString();
                cachedToken = objectMapper.readTree(body).path("token").asText(null);
                if (cachedToken == null) {
                    throw new IllegalStateException("Login failed, response: " + body);
                }
            } catch (IllegalStateException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException("Failed to obtain auth token", e);
            }
        }
        return cachedToken;
    }

    public void invalidateToken() {
        cachedToken = null;
    }
}
