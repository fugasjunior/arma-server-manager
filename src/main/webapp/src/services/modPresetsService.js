import http from "./httpService";
import config from "../config";

const apiEndpoint = config.apiUrl + "/mods/presets";

export function getPresets() {
    return http.get(apiEndpoint);
}

export function createPreset(body) {
    return http.post(apiEndpoint, body);
}

export function deletePreset(name) {
    return http.delete(apiEndpoint + "/" + name);
}