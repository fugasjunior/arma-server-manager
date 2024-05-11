import config from "../config"
import http from "./httpService";

const apiEndpoint = config.apiUrl + "/login";

export async function login(username: string, password: string) {
    const formData = new FormData();
    formData.append("username", username);
    formData.append("password", password);
    const {data} = await http.post(apiEndpoint, formData);
    return data;
}
