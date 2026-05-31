import {AxiosResponse} from "axios";
import {armaLauncherPresetApi, serversApi, steamCmdApi} from "./client";
import {scenariosApi} from "./client";

function triggerBrowserDownload(response: AxiosResponse, fallbackName: string) {
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement("a");
    link.href = url;
    let fileName = response.headers["content-disposition"]
        ?.split("filename=")[1]
        ?.replace(/"/g, "");
    if (!fileName || fileName.length === 0) {
        fileName = fallbackName;
    }
    link.setAttribute("download", fileName);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
}

export function downloadServerLog(id: number) {
    serversApi.downloadServerLog({id}, {responseType: "blob"}).then((response) => {
        triggerBrowserDownload(response, "log.txt");
    });
}

export function downloadSteamCmdLog() {
    steamCmdApi.downloadSteamCmdLog({responseType: "blob"}).then((response) => {
        triggerBrowserDownload(response, "log.txt");
    });
}

export function downloadScenario(serverId: number, name: string) {
    scenariosApi.downloadServerScenario({id: serverId, name}, {responseType: "blob"}).then((response) => {
        triggerBrowserDownload(response, name);
    });
}

export function downloadExportedPreset(id: number) {
    armaLauncherPresetApi.downloadLauncherPreset({id}, {responseType: "blob"}).then((response) => {
        triggerBrowserDownload(response, "exported_preset.html");
    });
}
