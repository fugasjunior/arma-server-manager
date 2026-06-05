import axios from "axios";
import {toast} from "react-toastify";
import {
    AdditionalServersApi,
    AppSettingsApi,
    ArmaLauncherPresetApi,
    Configuration,
    HeadlessClientApi,
    KeysApi,
    LocalModsApi,
    ModPresetsApi,
    ModsApi,
    PermissionsApi,
    RolesApi,
    ScenariosApi,
    ServerInstallationApi,
    ServersApi,
    SteamAuthApi,
    SteamCmdApi,
    SystemApi,
    UsersApi,
} from "./generated";
import config from "../config";

// withCredentials sends the session cookie (JSESSIONID) and allows Axios to auto-attach
// the XSRF-TOKEN cookie value as the X-XSRF-TOKEN header on mutating requests.
export const apiAxiosInstance = axios.create({baseURL: config.apiUrl, withCredentials: true});

apiAxiosInstance.interceptors.response.use(undefined, (error) => {
    const expectedError =
        error.response &&
        error.response.status >= 400 &&
        error.response.status < 500;

    // Skip toast for login endpoint errors (handled by component)
    const isLoginEndpoint = error.config?.url?.includes('/login');
    // Skip toast for local-mod 409 — component shows overwrite dialog instead
    const isLocalModConflict = error.config?.url?.includes('/local-mod') && error.response?.status === 409;
    if (isLoginEndpoint || isLocalModConflict) {
        return Promise.reject(error);
    }

    if (error.response?.status === 401) {
        // /users/me is the session bootstrap probe — 401 means "not logged in", not an error.
        // All other 401s mean the session expired mid-use: notify the auth context so it can
        // clear the user and let ProtectedRoute redirect to /login via the router (no hard reload).
        const isCurrentUserEndpoint = error.config?.url?.includes('/users/me');
        if (!isCurrentUserEndpoint) {
            window.dispatchEvent(new Event("auth:unauthorized"));
        }
        return Promise.reject(error);
    }

    if (!expectedError) {
        console.error(error.response?.data?.message);
        toast.error("An unexpected error occurred.");
    } else if (error.response?.data?.message) {
        toast.error(error.response.data.message);
    }

    return Promise.reject(error);
});

const apiConfig = new Configuration({basePath: ""});

export const serversApi = new ServersApi(apiConfig, "", apiAxiosInstance);
export const additionalServersApi = new AdditionalServersApi(apiConfig, "", apiAxiosInstance);
export const headlessClientApi = new HeadlessClientApi(apiConfig, "", apiAxiosInstance);
export const serverInstallationApi = new ServerInstallationApi(apiConfig, "", apiAxiosInstance);
export const modsApi = new ModsApi(apiConfig, "", apiAxiosInstance);
export const localModsApi = new LocalModsApi(apiConfig, "", apiAxiosInstance);
export const modPresetsApi = new ModPresetsApi(apiConfig, "", apiAxiosInstance);
export const armaLauncherPresetApi = new ArmaLauncherPresetApi(apiConfig, "", apiAxiosInstance);
export const scenariosApi = new ScenariosApi(apiConfig, "", apiAxiosInstance);
export const appSettingsApi = new AppSettingsApi(apiConfig, "", apiAxiosInstance);
export const keysApi = new KeysApi(apiConfig, "", apiAxiosInstance);
export const steamAuthApi = new SteamAuthApi(apiConfig, "", apiAxiosInstance);
export const steamCmdApi = new SteamCmdApi(apiConfig, "", apiAxiosInstance);
export const systemApi = new SystemApi(apiConfig, "", apiAxiosInstance);
export const usersApi = new UsersApi(apiConfig, "", apiAxiosInstance);
export const rolesApi = new RolesApi(apiConfig, "", apiAxiosInstance);
export const permissionsApi = new PermissionsApi(apiConfig, "", apiAxiosInstance);
