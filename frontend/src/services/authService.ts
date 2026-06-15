import {apiAxiosInstance} from "../api/client";

export async function login(username: string, password: string): Promise<void> {
    // Prime the XSRF-TOKEN cookie. After logout Spring's CsrfLogoutHandler clears
    // it; this GET re-seeds it via CsrfCookieFilter so the POST below carries a
    // valid token. The 401 (when not logged in) is expected and ignored.
    try {
        await apiAxiosInstance.get("/users/me");
    } catch {
        // ignore — the GET still set the cookie
    }

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
