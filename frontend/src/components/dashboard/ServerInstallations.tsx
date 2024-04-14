import {useEffect, useState} from "react";
import {Grid} from "@mui/material";
import {useInterval} from "../../hooks/use-interval";
import {getServerInstallations, installServer} from "../../services/serverInstallationsService";
import ServerInstallationItem from "./ServerInstallationItem";
import {ServerInstallationDto} from "../../dtos/ServerInstallationDto.ts";
import {ServerType} from "../../dtos/ServerDto.ts";

const ServerInstallations = () => {
    const [serverInstallations, setServerInstallations] = useState<Array<ServerInstallationDto>>([]);

    useEffect(() => {
        fetchServerInstallations();
    }, []);

    useInterval(() => fetchServerInstallations(), 5000);

    const fetchServerInstallations = async () => {
        const {data: serverInstallationsDto} = await getServerInstallations();
        const installations = serverInstallationsDto.serverInstallations
            .sort((a: ServerInstallationDto, b: ServerInstallationDto) => a.type.localeCompare(b.type));
        setServerInstallations(installations);
    };

    const handleUpdateClicked = async (serverType: ServerType) => {
        await installServer(serverType);

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
    }

    return (
        <>
            <Grid container spacing={2}>
                {serverInstallations.map(installation => (
                    <Grid item xs={12} md={6} key={installation.type}>
                        <ServerInstallationItem installation={installation}
                                                onUpdateClicked={handleUpdateClicked}
                        />
                    </Grid>
                ))}
            </Grid>
        </>
    );
};

export default ServerInstallations;