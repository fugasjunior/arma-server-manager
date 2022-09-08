import http from "./httpService";

const {apiUrl} = fetch("../config.json");
const apiEndpoint = apiUrl + "/config";

export function getAuth() {
    return http.get(apiEndpoint + "/auth");
}

export function setAuth(auth) {
    return http.post(apiEndpoint + "/auth", auth);
}