import {useEffect, useState} from "react";
import {Grid, SelectChangeEvent} from "@mui/material";
import {useInterval} from "../../hooks/use-interval";
import {changeServerBranch, getServerInstallations, installServer} from "../../services/serverInstallationsService";
import ServerInstallationItem from "./ServerInstallationItem";
import {ServerInstallationDto} from "../../dtos/ServerInstallationDto.ts";
import {ServerType} from "../../dtos/ServerDto.ts";
import {SteamCmdItemInfoDto} from "../../dtos/SteamCmdItemInfoDto.ts";
import {getItemInfo} from "../../services/steamCmdService.ts";

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
        const {data: serverInstallationsDto} = await getServerInstallations();
        const installations = serverInstallationsDto.serverInstallations
            .sort((a: ServerInstallationDto, b: ServerInstallationDto) => a.type.localeCompare(b.type));
        setServerInstallations(installations);
    };

    const fetchSteamCmdItemInfo = async () => {
        const {data} = await getItemInfo();
        setSteamCmdItemInfo(data);
    };

    const handleUpdateClicked = async (serverType: ServerType) => {
        await installServer(serverType);

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

            installation.installationStatus = "INSTALLATION_IN_PROGRESS";
            installation.errorStatus = null;
            return newState;
        });
    };

    const handleBranchChanged = async (e: SelectChangeEvent, serverType: ServerType) => {
        const selectedBranch = e.target.value;

        setServerInstallations(prevState => {
            const newState = [...prevState];
            const installation = newState.find(i => i.type === serverType);
            if (!installation) {
                return prevState;
            }

            installation.branch = selectedBranch;
            return newState;
        });

        await changeServerBranch(serverType, selectedBranch);
    };

    const serverTypeToId = (type: ServerType): number => {
        switch (type) {
            case ServerType.ARMA3:
                return 233780;
            case ServerType.REFORGER:
                return 1874900;
            case ServerType.DAYZ:
                return 223350;
            case ServerType.DAYZ_EXP:
                return 1042420;
        }
    }

    return (
        <>
            <Grid container spacing={2}>
                {serverInstallations.map(installation => (
                    <Grid item xs={12} md={6} key={installation.type}>
                        <ServerInstallationItem installation={installation}
                                                steamCmdItemInfo={steamCmdItemInfo[serverTypeToId(installation.type)]}
                                                onBranchChanged={handleBranchChanged}
                                                onUpdateClicked={handleUpdateClicked}
                        />
                    </Grid>
                ))}
            </Grid>
        </>
    );
};

export default ServerInstallations;