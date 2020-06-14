import http from "./httpService";
import {apiUrl} from "../config.json";

const apiEndpoint = apiUrl + "/additionalServers";

export function getServers() {
    return http.get(apiEndpoint);
}

export function startServer(id) {
    return http.post(apiEndpoint + "/start/" + id);
}

export function stopServer(id) {
    return http.post(apiEndpoint + "/stop/" + id);
}