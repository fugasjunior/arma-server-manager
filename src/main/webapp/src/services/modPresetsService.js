import http from "./httpService";
import config from "../config";

const apiEndpoint = config.apiUrl + "/mod/preset";

export function getModPreset(id) {
    return http.get(apiEndpoint + "/" + id);
}

export function getModPresets(filter) {
    let query = ""
    if (filter) {
        query = "?filter=" + filter;
    }

    return http.get(apiEndpoint + query);
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