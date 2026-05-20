import {Grid, SelectChangeEvent} from "@mui/material";
import {serverInstallationApi} from "../../api/client";
import {InstallationBranch, InstallationStatus, ServerInstallationDto, ServerType} from "../../api/generated";
import ServerInstallationItem from "./ServerInstallationItem";
import {useServerInstallations} from "../../hooks/queries/useServerInstallations";
import {useSteamCmdItemInfos} from "../../hooks/queries/useSteamCmdItemInfos";
import {useQueryClient} from "@tanstack/react-query";
import {queryKeys} from "../../api/queryKeys";
import {usePermission} from "../../hooks/usePermission";
import PermissionGuard from "../auth/PermissionGuard";

const ServerInstallations = () => {
    const queryClient = useQueryClient();
    const canViewInstall = usePermission("INSTALL_VIEW");
    const canPollSteamCmd = usePermission("STEAM_AUTH_ADMIN");
    const {data: serverInstallations = []} = useServerInstallations({refetchInterval: 5000, enabled: canViewInstall});
    const {data: steamCmdItemInfo = {}} = useSteamCmdItemInfos({refetchInterval: 5000, enabled: canPollSteamCmd});

    const handleUpdateClicked = async (serverType: ServerType) => {
        queryClient.setQueryData(queryKeys.serverInstallations, (old: ServerInstallationDto[] = []) =>
            old.map(i => i.type === serverType
                ? {...i, installationStatus: InstallationStatus.InstallationInProgress, errorStatus: undefined}
                : i
            )
        );
        queryClient.setQueryData(queryKeys.steamCmdItemInfos, (old: Record<string, unknown> = {}) => {
            const updated = {...old};
            delete updated[serverTypeToId(serverType)];
            return updated;
        });
        await serverInstallationApi.installServer({type: serverType});
        await queryClient.invalidateQueries({queryKey: queryKeys.serverInstallations});
    };

    const handleUninstallConfirmed = async (serverType: ServerType) => {
        await serverInstallationApi.uninstallServer({type: serverType});
        await queryClient.invalidateQueries({queryKey: queryKeys.serverInstallations});
    };

    const handleBranchChanged = async (e: SelectChangeEvent, serverType: ServerType) => {
        const selectedBranch = e.target.value as InstallationBranch;
        queryClient.setQueryData(queryKeys.serverInstallations, (old: ServerInstallationDto[] = []) =>
            old.map(i => i.type === serverType ? {...i, branch: selectedBranch} : i)
        );
        await serverInstallationApi.setActiveBranch({type: serverType, activeBranchDto: {branch: selectedBranch}});
    };

    const serverTypeToId = (type: ServerType): number => {
        switch (type) {
            case ServerType.Arma3: return 233780;
            case ServerType.Reforger: return 1874900;
            case ServerType.Dayz: return 223350;
            case ServerType.DayzExp: return 1042420;
            default: return 0;
        }
    };

    return (
        <PermissionGuard permission="INSTALL_VIEW">
            <Grid container spacing={2}>
                {serverInstallations.map(installation => (
                    <Grid size={{xs: 12, md: 6}} key={installation.type}>
                        <ServerInstallationItem installation={installation}
                                                steamCmdItemInfo={steamCmdItemInfo[serverTypeToId(installation.type!)]}
                                                onBranchChanged={handleBranchChanged}
                                                onUpdateClicked={handleUpdateClicked}
                                                onUninstallConfirmed={handleUninstallConfirmed}
                        />
                    </Grid>
                ))}
            </Grid>
        </PermissionGuard>
    );
};

export default ServerInstallations;
