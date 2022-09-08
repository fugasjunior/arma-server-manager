import http from "./httpService";

const {apiUrl} = fetch("../config.json");
const apiEndpoint = apiUrl + "/system";

export function getFreeSpace() {
    return http.get(apiEndpoint + "/space");
}

export function getSystemInfo() {
    return http.get(apiEndpoint + "/info");
}