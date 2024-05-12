import http from "./httpService";
import config from "../config";

const apiEndpoint = config.apiUrl + "/steamcmd";

export function getItemInfo() {
    return http.get(apiEndpoint);
}
