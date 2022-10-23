import http from "./httpService";
import config from "../config";

const apiEndpoint = config.apiUrl + "/additionalServers";

export function getAdditionalServer(id) {
    return http.get(apiEndpoint + "/" + id);
}

export function getAdditionalServers() {
    return http.get(apiEndpoint);
}

export function startAdditionalServer(id) {
    return http.post(apiEndpoint + "/" + id + "/start");
}

export function stopAdditionalServer(id) {
    return http.post(apiEndpoint + "/" + id + "/stop");
}