import http from "./httpService";
import config from "../config";

const apiEndpoint = config.apiUrl + "/server";

export function getServerLogs(id) {
    return http.get(apiEndpoint + "/" + id + "/log");
}
