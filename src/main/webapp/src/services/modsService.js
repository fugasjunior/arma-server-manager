import http from "./httpService";

const {apiUrl} = fetch("../config.json");
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
    return http.post(apiEndpoint + "/updateAll");
}

export function setActive(modId, val) {
    return http.post(apiEndpoint + "/setActive/" + modId + "/?active=" + val);
}

export function setMultipleActive(mods) {
    return http.post(apiEndpoint + "/setMultipleActive", mods);
}