package cz.forgottenempire.servermanager.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.File;
import java.nio.file.Files;
import java.util.function.Supplier;

public class Api {

    private final MockMvc mockMvc;
    private final Supplier<String> token;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public Api(MockMvc mockMvc, Supplier<String> token) {
        this.mockMvc = mockMvc;
        this.token = token;
    }

    public ResultActions get(String path) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.get(path)
                .header("Authorization", token.get()));
    }

    public ResultActions post(String path) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.post(path)
                .header("Authorization", token.get()));
    }

    public ResultActions post(String path, Object body) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.post(path)
                .header("Authorization", token.get())
                .contentType(MediaType.APPLICATION_JSON)
                .content(MAPPER.writeValueAsString(body)));
    }

    public ResultActions postJson(String path, String json) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.post(path)
                .header("Authorization", token.get())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
    }

    public ResultActions postWithQueryParam(String path, String paramName, String value) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.post(path)
                .queryParam(paramName, value)
                .header("Authorization", token.get()));
    }

    public ResultActions put(String path, String json) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.put(path)
                .header("Authorization", token.get())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
    }

    public ResultActions delete(String path) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.delete(path)
                .header("Authorization", token.get()));
    }

    public ResultActions multipartPost(String path, String paramName, File file, String contentType) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.multipart(path)
                .file(new MockMultipartFile(paramName, file.getName(), contentType,
                        Files.readAllBytes(file.toPath())))
                .header("Authorization", token.get()));
    }

    public ResultActions multipartPostWithParam(String path, String paramName, File file, String contentType, String queryParamName, String queryParamValue) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.multipart(path)
                .file(new MockMultipartFile(paramName, file.getName(), contentType,
                        Files.readAllBytes(file.toPath())))
                .param(queryParamName, queryParamValue)
                .header("Authorization", token.get()));
    }

    public ResultActions patch(String path, String json) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.patch(path)
                .header("Authorization", token.get())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
    }
}
