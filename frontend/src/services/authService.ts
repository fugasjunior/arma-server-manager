import {apiAxiosInstance} from "../api/client";

export async function login(username: string, password: string): Promise<void> {
    const body = new URLSearchParams();
    body.append("username", username);
    body.append("password", password);
    await apiAxiosInstance.post("/login", body, {
        headers: {"Content-Type": "application/x-www-form-urlencoded"},
    });
}

export async function logout(): Promise<void> {
    await apiAxiosInstance.post("/logout");
}
