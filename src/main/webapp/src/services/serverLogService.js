import http from "./httpService";
import config from "../config";

const apiEndpoint = config.apiUrl + "/server";

export function getServerLogs(id) {
    return http.get(apiEndpoint + "/" + id + "/log");
}

export function downloadLogFile(id) {
    http.get(apiEndpoint + "/" + id + "/log/download", {responseType: 'blob'})
        .then(response => {
            console.log(response);
            const url = window.URL.createObjectURL(new Blob([response.data]));
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', response.headers['content-disposition'].split('filename=')[1].replace(/"/g, ''));
            document.body.appendChild(link);
            link.click();
            link.parentNode.removeChild(link);
        })
}
