import http from "./httpService";
import config from "../config";

const apiEndpoint = config.apiUrl + "/mod/preset";

export function getModPreset(id) {
    return http.get(apiEndpoint + "/" + id);
}

export function getModPresets(filter) {
    let query = ""
    if (filter) {
        query = "?filter=" + filter;
    }

    return http.get(apiEndpoint + query);
}

export function createModPreset(preset) {
    return http.post(apiEndpoint, preset);
}

export function updateModPreset(id, preset) {
    return http.put(apiEndpoint + "/" + id, preset);
}

export function deleteModPreset(id) {
    return http.delete(apiEndpoint + "/" + id);
}

export function downloadExportedPreset(id) {
    http.get(config.apiUrl + "/mod/launcher_preset/" + id, {responseType: 'blob'})
        .then(response => {
            const url = window.URL.createObjectURL(new Blob([response.data]));
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', "exported_preset.html");
            document.body.appendChild(link);
            link.click();
            link.parentNode.removeChild(link);
        })
}

export function uploadImportedPreset(file) {
    const formData = new FormData();
    formData.append('preset', file);
    return http.post(config.apiUrl + "/mod/launcher_preset/", formData);
}