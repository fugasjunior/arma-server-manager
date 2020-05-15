import http from "./httpService";
import {apiUrl} from "../config.json";

const apiEndpoint = apiUrl + "/system";

export function getFreeSpace() {
    return http.get(apiEndpoint + "/space");
}