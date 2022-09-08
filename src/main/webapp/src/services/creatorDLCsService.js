import http from "./httpService";
import config from "../config";

const apiEndpoint = config.apiUrl + "/creatordlcs";

export function getCreatorDLCs() {
    return http.get(apiEndpoint);
}

export function updateCreatorDLC(creatorDlc) {
    return http.put(apiEndpoint + "/" + creatorDlc.id, creatorDlc);
}
