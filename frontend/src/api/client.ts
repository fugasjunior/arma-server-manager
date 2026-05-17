import axios from "axios";
import {toast} from "react-toastify";
import {
    AdditionalServersApi,
    ArmaLauncherPresetApi,
    Configuration,
    HeadlessClientApi,
    ModPresetsApi,
    ModsApi,
    ScenariosApi,
    ServerInstallationApi,
    ServersApi,
    SteamAuthApi,
    SteamCmdApi,
    SystemApi,
} from "./generated";
import config from "../config";

export const apiAxiosInstance = axios.create({baseURL: config.apiUrl});

apiAxiosInstance.interceptors.response.use(undefined, (error) => {
    const expectedError =
        error.response &&
        error.response.status >= 400 &&
        error.response.status < 500;

    if (!expectedError) {
        console.error(error.response?.data?.message);
        toast.error("An unexpected error occurred.");
    } else if (error.response?.data?.message) {
        toast.error(error.response.data.message);
    }

    return Promise.reject(error);
});

export function setJwt(jwt: string) {
    apiAxiosInstance.defaults.headers.common["Authorization"] = jwt;
}

const apiConfig = new Configuration({basePath: ""});

export const serversApi = new ServersApi(apiConfig, "", apiAxiosInstance);
export const additionalServersApi = new AdditionalServersApi(apiConfig, "", apiAxiosInstance);
export const headlessClientApi = new HeadlessClientApi(apiConfig, "", apiAxiosInstance);
export const serverInstallationApi = new ServerInstallationApi(apiConfig, "", apiAxiosInstance);
export const modsApi = new ModsApi(apiConfig, "", apiAxiosInstance);
export const modPresetsApi = new ModPresetsApi(apiConfig, "", apiAxiosInstance);
export const armaLauncherPresetApi = new ArmaLauncherPresetApi(apiConfig, "", apiAxiosInstance);
export const scenariosApi = new ScenariosApi(apiConfig, "", apiAxiosInstance);
export const steamAuthApi = new SteamAuthApi(apiConfig, "", apiAxiosInstance);
export const steamCmdApi = new SteamCmdApi(apiConfig, "", apiAxiosInstance);
export const systemApi = new SystemApi(apiConfig, "", apiAxiosInstance);
