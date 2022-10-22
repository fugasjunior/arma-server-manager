import http from "./httpService";
import config from "../config";

const apiEndpoint = config.apiUrl + "/mod";

export function getMods() {
    return http.get(apiEndpoint);
}

export function installMod(modId) {
    return http.post(apiEndpoint + "/" + modId);
}

export function uninstallMod(modId) {
    return http.delete(apiEndpoint + "/" + modId);
}

export function updateAllMods() {
    return http.post(apiEndpoint + "/update");
}