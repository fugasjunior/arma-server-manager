import http from "./httpService";
import config from "../config";

const apiEndpoint = config.apiUrl + "/server";

export function getServers() {
    return http.get(apiEndpoint);
}

export function getServer(id) {
    return http.get(apiEndpoint + "/" + id);
}

export function createServer(serverInfo) {
    return http.post(apiEndpoint, serverInfo);
}

export function updateServer(id, serverInfo) {
    return http.put(apiEndpoint + "/" + id, serverInfo);
}

export function startServer(id) {
    return http.post(apiEndpoint + "/" + id + "/start");
}

export function stopServer(id) {
    return http.post(apiEndpoint + "/" + id + "/stop");
}

export function restartServer(id) {
    return http.post(apiEndpoint + "/" + id + "/restart");
}

export function deleteServer(id) {
    return http.delete(apiEndpoint + "/" + id);
}
