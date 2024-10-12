package cz.forgottenempire.servermanager.workshop.metadata;

import com.fasterxml.jackson.databind.JsonNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

class JsonPropertyProvider implements PropertyProvider {

    private final JsonNode modInfoJson;

    JsonPropertyProvider(JsonNode modInfoJson) {
        this.modInfoJson = modInfoJson;
    }

    @Override
    public String findName() {
        return getValueFromJson("title", modInfoJson);
    }

    @Override
    public String findConsumerAppId() {
        return getValueFromJson("consumer_appid", modInfoJson);
    }

    private String getValueFromJson(String key, JsonNode modInfoJson) {
        JsonNode value = modInfoJson.findValue(key);
        return value != null ? value.asText() : null;
    }
}
