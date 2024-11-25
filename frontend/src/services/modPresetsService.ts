import http from "./httpService";
import config from "../config";
import {ModPresetRequestDto, ModPresetRenameDto} from "../dtos/ModPresetDto.ts";

const apiEndpoint = config.apiUrl + "/mod/preset";

export function getModPreset(id: string) {
    return http.get(apiEndpoint + "/" + id);
}

export function getModPresets(filter?: string) {
    let query = ""
    if (filter) {
        query = "?filter=" + filter;
    }

    return http.get(apiEndpoint + query);
}

export function createModPreset(preset: ModPresetRequestDto) {
    return http.post(apiEndpoint, preset);
}

export function updateModPreset(id: string, preset: ModPresetRequestDto) {
    return http.put(apiEndpoint + "/" + id, preset);
}

export function renameModPreset(id: string, preset: ModPresetRenameDto) {
    return http.put(apiEndpoint + "/rename/" + id, preset);
}

export function deleteModPreset(id: string) {
    return http.delete(apiEndpoint + "/" + id);
}

export function downloadExportedPreset(id: string) {
    http.get(config.apiUrl + "/mod/launcher_preset/" + id, {responseType: 'blob'})
        .then(response => {
            const url = window.URL.createObjectURL(new Blob([response.data]));
            const link = document.createElement('a');
            link.href = url;

            let fileName = response.headers['content-disposition']
                .split('filename=')[1]
                .replace(/"/g, '');
            if (!fileName || fileName.length === 0) {
                fileName = "exported_preset.html";
            }

            link.setAttribute('download', fileName);
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
        })
}

export function uploadImportedPreset(file: File) {
    const formData = new FormData();
    formData.append('preset', file);
    return http.post(config.apiUrl + "/mod/launcher_preset", formData);
}