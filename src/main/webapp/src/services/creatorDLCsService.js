import http from "./httpService";

const {apiUrl} = fetch("../config.json");
const apiEndpoint = apiUrl + "/creatordlcs";

export function getCreatorDLCs() {
    return http.get(apiEndpoint);
}

export function updateCreatorDLC(creatorDlc) {
    return http.put(apiEndpoint + "/" + creatorDlc.id, creatorDlc);
}
