import http from "./httpService";
import {apiUrl} from "../config.json";

const apiEndpoint = apiUrl + "/additionalServers";

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