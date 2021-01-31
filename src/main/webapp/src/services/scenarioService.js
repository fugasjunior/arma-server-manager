import http from "./httpService";
import {apiUrl} from "../config.json";

const apiEndpoint = apiUrl + "/scenarios";

export function getScenarios() {
    return http.get(apiEndpoint);
}

export function downloadScenario(name) {
    // return window.open(apiEndpoint + "/" + name);
    // return http.post()
    http.get(apiEndpoint + "/" + name, {responseType: 'blob'})
    .then(response => {
        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', name);
        // 3. Append to html page
        document.body.appendChild(link);
        // 4. Force download
        link.click();
        // 5. Clean up and remove the link
        link.parentNode.removeChild(link);
    })
}

export function uploadScenario(formData, config) {
    return http.post(apiEndpoint, formData, config);
}

export function deleteScenario(name) {
    return http.delete(apiEndpoint + "/" + name);
}