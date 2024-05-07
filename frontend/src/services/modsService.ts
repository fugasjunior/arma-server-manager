import http from "./httpService";
import config from "../config";

const apiEndpoint = config.apiUrl + "/mod";

export function getMods(filterByServerType?: string) {
    const query = filterByServerType ? "?filter=" + filterByServerType : "";
    return http.get(apiEndpoint + query);
}

export function installMod(modId: number) {
    return http.post(apiEndpoint + "/" + modId);
}

export function updateMods(modIdsList: string) {
    return http.post(apiEndpoint + "?modIds=" + modIdsList);
}

export function uninstallMods(modIdsList: string) {
    return http.delete(apiEndpoint + "?modIds=" + modIdsList);
}

export function setModServerOnly(modId: number, isServerOnly: boolean) {
    return http.patch(apiEndpoint + "/" + modId, {serverOnly: isServerOnly});
}
