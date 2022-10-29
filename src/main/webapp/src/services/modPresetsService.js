import http from "./httpService";
import config from "../config";

const apiEndpoint = config.apiUrl + "/mod/preset";

export function getModPreset(id) {
    return http.get(apiEndpoint + "/" + id);
}

export function getModPresets() {
    return http.get(apiEndpoint);
}

export function createModPreset(preset) {
    return http.post(apiEndpoint, preset);
}

export function updateModPreset(id, preset) {
    return http.put(apiEndpoint + "/" + id, preset);
}

export function deleteModPreset(id) {
    return http.post(apiEndpoint + "/" + id);
}