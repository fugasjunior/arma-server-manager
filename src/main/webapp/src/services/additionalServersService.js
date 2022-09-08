import http from "./httpService";
import config from "../config";

const apiEndpoint = config.apiUrl + "/additionalServers";

export function getServer(id) {
    return http.get(apiEndpoint + "/" + id);
}

export function getServers() {
    return http.get(apiEndpoint);
}

export function startServer(id) {
    return http.post(apiEndpoint + "/" + id + "/start");
}

export function stopServer(id) {
    return http.post(apiEndpoint + "/" + id + "/stop");
}