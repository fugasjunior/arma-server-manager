import http from "./httpService";
import config from "../config";

const apiEndpoint = config.apiUrl + "/scenarios";

export function getScenarios() {
    return http.get(apiEndpoint);
}

export function getReforgerScenarios() {
    return http.get(apiEndpoint + "/REFORGER");
}

export function downloadScenario(name: string) {
    http.get(apiEndpoint + "/" + name, {responseType: 'blob'})
        .then(response => {
            const url = window.URL.createObjectURL(new Blob([response.data]));
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', name);
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
        })
}

export function uploadScenario(formData: FormData, config: any) {
    return http.post(apiEndpoint, formData, config);
}

export function deleteScenario(name: string) {
    return http.delete(apiEndpoint + "/" + name);
}