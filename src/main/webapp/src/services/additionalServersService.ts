import http from "./httpService";
import config from "../config";

const apiEndpoint = config.apiUrl + "/additionalServers";

export function getAdditionalServer(id: number) {
    return http.get(apiEndpoint + "/" + id);
}

export function getAdditionalServers() {
    return http.get(apiEndpoint);
}

export function startAdditionalServer(id: number) {
    return http.post(apiEndpoint + "/" + id + "/start");
}

export function stopAdditionalServer(id: number) {
    return http.post(apiEndpoint + "/" + id + "/stop");
}