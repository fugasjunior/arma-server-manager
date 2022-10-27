import http from "./httpService";
import config from "../config";

const apiEndpoint = config.apiUrl + "/server/installation";

export function getServerInstallations() {
    return http.get(apiEndpoint);
}

export function getServerInstallation(type) {
    return http.get(apiEndpoint + "/" + type);
}

export function installServer(type) {
    return http.post(apiEndpoint + "/" + type);
}

