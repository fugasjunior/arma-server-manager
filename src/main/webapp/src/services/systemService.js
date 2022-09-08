import http from "./httpService";
import config from "../config";

const apiEndpoint = config.apiUrl + "/system";

export function getFreeSpace() {
    return http.get(apiEndpoint + "/space");
}

export function getSystemInfo() {
    return http.get(apiEndpoint + "/info");
}