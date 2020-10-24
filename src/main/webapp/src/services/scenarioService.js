import http from "./httpService";
import {apiUrl} from "../config.json";

const apiEndpoint = apiUrl + "/scenarios";

export function getScenarios() {
    return http.get(apiEndpoint);
}

export function downloadScenario(name) {
    return window.open(apiEndpoint + "/" + name);
}

export function uploadScenario(formData, config) {
    return http.post(apiEndpoint, formData, config);
}

export function deleteScenario(name) {
    return http.delete(apiEndpoint + "/" + name);
}