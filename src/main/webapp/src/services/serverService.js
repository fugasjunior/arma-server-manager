import http from "./httpService";
import config from "../config";

const apiEndpoint = config.apiUrl + "/server";

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

export function queryServer() {
    return http.get(apiEndpoint + "/query");
}

export function getServerStatus() {
    return http.get(apiEndpoint + "/status");
}

export function updateServer() {
    return http.post(apiEndpoint + "/update");
}