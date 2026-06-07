package cz.forgottenempire.servermanager.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@Component
@Profile("integrationtest")
public class AuthTestHelper {

    @Value("${auth.username}")
    private String username;

    @Value("${auth.password}")
    private String password;

    @Autowired
    private MockMvc mockMvc;

    private MockHttpSession cachedSession;

    public MockHttpSession getSession() {
        if (cachedSession == null) {
            try {
                MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/login")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                                .param("username", username)
                                .param("password", password)
                ).andReturn();

                cachedSession = (MockHttpSession) result.getRequest().getSession(false);
                if (cachedSession == null) {
                    throw new IllegalStateException("Login failed: no session created. Response: "
                            + result.getResponse().getContentAsString());
                }
            } catch (IllegalStateException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException("Failed to obtain auth session", e);
            }
        }
        return cachedSession;
    }

    public void invalidateSession() {
        cachedSession = null;
    }
}
