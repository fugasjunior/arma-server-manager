import http from "./httpService";
import {apiUrl} from "../config.json";

const apiEndpoint = apiUrl + "/scenarios";

export function getScenarios() {
    return http.get(apiEndpoint);
}

export function uploadScenario(formData) {
    return http.post(apiEndpoint + "/upload", formData);
}

export function deleteScenario(name) {
    return http.delete(apiEndpoint + "/delete/" + name);
}