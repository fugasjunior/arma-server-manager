import http from "./httpService";
import config from "../config";

const apiEndpoint = config.apiUrl + "/system";

export function getSystemInfo() {
    return http.get(apiEndpoint);
}

export function getServerOS() {
    return http.get(apiEndpoint + "/os");
}