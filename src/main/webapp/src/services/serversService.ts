import http from "./httpService";
import config from "../config";
import {ServerDto} from "../dtos/ServerDto.ts";

const apiEndpoint = config.apiUrl + "/server";

export function getServers() {
    return http.get(apiEndpoint);
}

export function getServer(id: number) {
    return http.get(apiEndpoint + "/" + id);
}

export function createServer(serverInfo: ServerDto) {
    return http.post(apiEndpoint, serverInfo);
}

export function updateServer(id: number, serverInfo: any) { // TODO
    return http.put(apiEndpoint + "/" + id, serverInfo);
}

export function startServer(id: number) {
    return http.post(apiEndpoint + "/" + id + "/start");
}

export function stopServer(id: number) {
    return http.post(apiEndpoint + "/" + id + "/stop");
}

export function restartServer(id: number) {
    return http.post(apiEndpoint + "/" + id + "/restart");
}

export function deleteServer(id: number) {
    return http.delete(apiEndpoint + "/" + id);
}

export function getServerStatus(id: number) {
    return http.get(apiEndpoint + "/" + id + "/status");
}
