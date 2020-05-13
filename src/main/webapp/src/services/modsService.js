import http from "./httpService";
import {apiUrl} from "../config.json";

const apiEndpoint = apiUrl + "/mods";

export function getMods() {
    return http.get(apiEndpoint);
}

export function installMod(modId) {
    return http.post(apiEndpoint + "/install/" + modId);
}

export function uninstallMod(modId) {
    return http.delete(apiEndpoint + "/uninstall/" + modId);
}

export function refreshMods() {
    return http.post(apiEndpoint + "/refresh");
}

export function setActive(modId, val) {
    return http.post(apiEndpoint + "/setActive/" + modId + "/?active=" + val);
}