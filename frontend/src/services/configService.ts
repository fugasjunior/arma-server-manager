import http from "./httpService";
import config from "../config";
import {SteamAuthDto} from "../dtos/SteamAuthDto.ts";

const apiEndpoint = config.apiUrl + "/config";

export function getAuth() {
    return http.get(apiEndpoint + "/auth");
}

export function setAuth(auth: SteamAuthDto) {
    return http.post(apiEndpoint + "/auth", auth);
}

export function clearAuth() {
    return http.delete(apiEndpoint + "/auth");
}