import {ServerType} from "./generated";

export const queryKeys = {
    servers: ["servers"] as const,
    server: (id: number) => ["servers", id] as const,
    serverStatus: (id: number) => ["servers", id, "status"] as const,
    mods: (filter?: ServerType) => ["mods", filter ?? "all"] as const,
    presets: (filter?: ServerType) => ["presets", filter ?? "all"] as const,
    steamCmdItemInfos: ["steamCmdItemInfos"] as const,
    additionalServers: ["additionalServers"] as const,
    systemDetails: ["systemDetails"] as const,
    serverInstallations: ["serverInstallations"] as const,
    creatorDlcs: (filter?: ServerType) => ["creatorDlcs", filter ?? "all"] as const,
    arma3Scenarios: ["arma3Scenarios"] as const,
    reforgerScenarios: ["reforgerScenarios"] as const,
} as const;
