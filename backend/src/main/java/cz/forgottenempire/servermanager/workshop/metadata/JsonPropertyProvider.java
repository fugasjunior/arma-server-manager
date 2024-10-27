package cz.forgottenempire.servermanager.workshop.metadata;

import com.fasterxml.jackson.databind.JsonNode;

class JsonPropertyProvider {

    private final JsonNode modInfoJson;

    JsonPropertyProvider(JsonNode modInfoJson) {
        this.modInfoJson = modInfoJson;
    }

    public String findName() {
        return getValueFromJson("title", modInfoJson);
    }

    public String findConsumerAppId() {
        return getValueFromJson("consumer_appid", modInfoJson);
    }

    private String getValueFromJson(String key, JsonNode modInfoJson) {
        JsonNode value = modInfoJson.findValue(key);
        return value != null ? value.asText() : null;
    }
}
