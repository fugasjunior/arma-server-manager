import http from "./httpService";
import config from "../config";

const apiEndpoint = config.apiUrl + "/config";

export function getAuth() {
    return http.get(apiEndpoint + "/auth");
}

export function setAuth(auth) {
    return http.post(apiEndpoint + "/auth", auth);
}