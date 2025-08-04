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

/**
 * Checks if Steam authentication is configured
 * @returns Promise with isConfigured status
 */
export function getAuthStatus() {
    return http.get(apiEndpoint + "/auth/status");
}

/**
 * Verifies Steam credentials and detects 2FA requirements
 * @param auth Steam credentials to verify
 * @returns Promise with verification result
 */
export function verifyCredentials(auth: SteamAuthDto) {
    return http.post(apiEndpoint + "/auth/verify", auth);
}
