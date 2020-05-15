import http from "./httpService";
import {apiUrl} from "../config.json";

const apiEndpoint = apiUrl + "/server";

export function getServerSettings() {
    return http.get(apiEndpoint + "/settings");
}

export function setServerSettings(settings) {
    return http.post(apiEndpoint + "/settings", settings);
}

export function startServer() {
    return http.post(apiEndpoint + "/start");
}

export function stopServer() {
    return http.post(apiEndpoint + "/stop");
}

export function restartServer() {
    return http.post(apiEndpoint + "/restart");
}

export function getStatus() {
    return http.get(apiEndpoint + "/status");
}

export function getServerProcessAlive() {
    return http.get(apiEndpoint + "/alive");
}