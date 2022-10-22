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

export function refreshMods() {
    return http.post(apiEndpoint + "/updateAll");
}

export function setActive(modId, val) {
    return http.post(apiEndpoint + "/setActive/" + modId + "/?active=" + val);
}

export function setMultipleActive(mods) {
    return http.post(apiEndpoint + "/setMultipleActive", mods);
}