import {useEffect, useState} from "react";
import {Grid, SelectChangeEvent} from "@mui/material";
import {useInterval} from "../../hooks/use-interval";
import {serverInstallationApi, steamCmdApi} from "../../api/client";
import {InstallationBranch, InstallationStatus, ServerInstallationDto, ServerType, SteamCmdItemInfoDto} from "../../api/generated";
import ServerInstallationItem from "./ServerInstallationItem";

type WorkshopItemInfoResponse = {
    [id: number]: SteamCmdItemInfoDto
}

const ServerInstallations = () => {
    const [serverInstallations, setServerInstallations] = useState<Array<ServerInstallationDto>>([]);
    const [steamCmdItemInfo, setSteamCmdItemInfo] = useState<WorkshopItemInfoResponse>({});

    useEffect(() => {
        void fetchServerInstallations();
        void fetchSteamCmdItemInfo();
    }, []);

    useInterval(() => {
        void fetchServerInstallations();
        void fetchSteamCmdItemInfo();
    }, 5000);

    const fetchServerInstallations = async () => {
        const {data: serverInstallationsDto} = await serverInstallationApi.getServerInstallations();
        const installations = (serverInstallationsDto.serverInstallations ?? [])
            .sort((a: ServerInstallationDto, b: ServerInstallationDto) => (a.type ?? "").localeCompare(b.type ?? ""));
        setServerInstallations(installations);
    };

    const fetchSteamCmdItemInfo = async () => {
        const {data} = await steamCmdApi.getSteamCmdItemInfos();
        setSteamCmdItemInfo(data);
    };

    const handleUpdateClicked = async (serverType: ServerType) => {
        await serverInstallationApi.installServer({type: serverType});

        setSteamCmdItemInfo(prevState => {
            const newState = {...prevState};
            delete newState[serverTypeToId(serverType)];
            return newState;
        })

        setServerInstallations(prevState => {
            const newState = [...prevState];
            const installation = newState.find(i => i.type === serverType);
            if (!installation) {
                return prevState;
            }

            installation.installationStatus = InstallationStatus.InstallationInProgress;
            installation.errorStatus = undefined;
            return newState;
        });
    };

    const handleUninstallConfirmed = async (serverType: ServerType) => {
        await serverInstallationApi.uninstallServer({type: serverType});
        await fetchServerInstallations();
    };

    const handleBranchChanged = async (e: SelectChangeEvent, serverType: ServerType) => {
        const selectedBranch = e.target.value;

        setServerInstallations(prevState => {
            const newState = [...prevState];
            const installation = newState.find(i => i.type === serverType);
            if (!installation) {
                return prevState;
            }

            installation.branch = selectedBranch as InstallationBranch;
            return newState;
        });

        await serverInstallationApi.setActiveBranch({type: serverType, activeBranchDto: {branch: selectedBranch as InstallationBranch}});
    };

    const serverTypeToId = (type: ServerType): number => {
        switch (type) {
            case ServerType.Arma3:
                return 233780;
            case ServerType.Reforger:
                return 1874900;
            case ServerType.Dayz:
                return 223350;
            case ServerType.DayzExp:
                return 1042420;
            default:
                return 0;
        }
    }

    return (
        <>
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
        </>
    );
};

export default ServerInstallations;