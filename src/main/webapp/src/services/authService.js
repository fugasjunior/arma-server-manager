import http from "./httpService";
import jwt_decode from "jwt-decode";

const apiEndpoint = "/login";
const tokenKey = "token";

if (isAuthenticated()) {
    http.setJwt(getJwt());
}

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
    try {
        const jwtEncoded = getJwt();
        if (!jwtEncoded) {
            return false;
        }
        const jwt = jwt_decode(jwtEncoded.split(" ")[1]);
        return isJwtValid(jwt);
    } catch (err) {
        console.error(err);
        return false;
    }
}

function isJwtValid(jwt) {
    return Date.now() <= jwt.exp * 1000;
}
