import http from "./httpService";
import config from "../config";

const apiEndpoint = config.apiUrl + "/steamcmd";

export function getItemInfo() {
    return http.get(apiEndpoint);
}

export function getLogs() {
    return http.get(apiEndpoint + "/log");
}

export function downloadLogFile() {
    http.get(apiEndpoint + "/log/download", {responseType: 'blob'})
        .then(response => {
            const url = window.URL.createObjectURL(new Blob([response.data]));
            const link = document.createElement('a');
            link.href = url;

            let fileName = response.headers['content-disposition']
                .split('filename=')[1]
                .replace(/"/g, '');
            if (!fileName || fileName.length === 0) {
                fileName = "log.txt";
            }

            link.setAttribute('download', fileName);
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
        })
}