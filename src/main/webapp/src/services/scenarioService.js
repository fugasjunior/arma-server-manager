import http from "./httpService";
import {apiUrl} from "../config.json";

const apiEndpoint = apiUrl + "/scenarios";

export function getScenarios() {
    return http.get(apiEndpoint);
}

export function downloadScenario(name) {
    return http.get(apiEndpoint + "/download/" + name);
}

export function uploadScenario(formData, config) {
    return http.post(apiEndpoint + "/upload", formData, config);
}

export function deleteScenario(name) {
    return http.delete(apiEndpoint + "/delete/" + name);
}