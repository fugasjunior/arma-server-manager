import {useEffect, useState} from "react";
import {Grid} from "@mui/material";
import {useInterval} from "../../hooks/use-interval";
import {getServerInstallations, installServer} from "../../services/serverInstallationsService";
import ServerInstallationItem from "./ServerInstallationItem";

const ServerInstallations = () => {
    const [serverInstallations, setServerInstallations] = useState([]);

    useEffect(() => {
        fetchServerInstallations();
    }, []);

    useInterval(() => fetchServerInstallations(), 5000);

    const fetchServerInstallations = async () => {
        const {data: serverInstallationsDto} = await getServerInstallations();
        const installations = serverInstallationsDto.serverInstallations.sort((a, b) => a.name.localeCompare(b.name));
        setServerInstallations(installations);
    };

    const handleUpdateClicked = async (installationType) => {
        await installServer(installationType);

        setServerInstallations(prevState => {
            const newState = [...prevState];
            const installation = newState.find(i => i.type === installationType);
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