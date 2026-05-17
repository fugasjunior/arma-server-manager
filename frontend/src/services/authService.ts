import {apiAxiosInstance} from "../api/client";

const apiEndpoint = "/login";

export async function login(username: string, password: string) {
    const formData = new FormData();
    formData.append("username", username);
    formData.append("password", password);
    const {data} = await apiAxiosInstance.post(apiEndpoint, formData);
    return data;
}
