import http from "./httpService";
import config from "../config";
import {CreatorDlcDto} from "../dtos/CreatorDlcDto.ts";

const apiEndpoint = config.apiUrl + "/creatordlcs";

export function getCreatorDLCs() {
    return http.get(apiEndpoint);
}

export function updateCreatorDLC(creatorDlc: CreatorDlcDto) {
    return http.put(apiEndpoint + "/" + creatorDlc.id, creatorDlc);
}
