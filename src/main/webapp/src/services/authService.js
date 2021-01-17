import http from "./httpService";

const apiEndpoint = "/login";
const tokenKey = "token";

http.setJwt(getJwt());

export async function login(username, password) {
    const formData = new FormData();
    formData.append("username", username);
    formData.append("password", password);
    const {data} = await http.post(apiEndpoint, formData);
    const token = data["Authorization"];
    localStorage.setItem(tokenKey, token);
}

export function logout() {
    localStorage.removeItem(tokenKey);
}

export function getJwt() {
    return localStorage.getItem(tokenKey);
}

export function isAuthenticated() {
    return !!getJwt();
}