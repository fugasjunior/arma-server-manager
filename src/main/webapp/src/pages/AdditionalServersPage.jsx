import React, {useEffect, useState} from "react";
import {useInterval} from "../hooks/use-interval";
import {Avatar, Button, Divider, Stack, Typography} from "@mui/material";
import {
    getAdditionalServers,
    getServers, startAdditionalServer,
    startServer,
    stopAdditionalServer,
    stopServer
} from "../services/additionalServersService";
import Paper from "@mui/material/Paper";

const AdditionalServersPage = () => {

    const [additionalServers, setAdditionalServers] = useState([]);

    useEffect(() => {
        fetchAdditionalServers()
    }, [])

    useInterval(async () => {
        fetchAdditionalServers();
    }, 2000);

    const fetchAdditionalServers = async () => {
        const {data: servers} = await getAdditionalServers();
        setAdditionalServers(servers.servers);
    }

    const handleStart = async (id) => {
        await startAdditionalServer(id);
        await fetchAdditionalServers();
    };

    const handleStop = async (id) => {
        await stopAdditionalServer(id);
        await fetchAdditionalServers();
    };

    return (
            <>
                <Typography variant="h4" component="h2" mb={2}>Additional servers</Typography>
                {additionalServers.length === 0 && <p>No additional servers set up.</p>}
                <Paper>
                    <Stack p={3} spacing={2} divider={<Divider orientation="horizontal" flexItem/>}>
                        {additionalServers.map(server => (
                                <Stack key={server.id} direction="row" justifyContent="space-between" width="100%">
                                    <Stack direction="row" spacing={3}>
                                        <Avatar src={server.imageUrl} alt={`${server.name} icon`}/>
                                        <p>{server.name}</p>
                                    </Stack>
                                    {server.startedAt && <div>Started at: {server.startedAt}</div>}
                                    {server.alive &&
                                            <Button variant="contained" color="error"
                                                    onClick={() => handleStop(server.id)}>
                                                Stop
                                            </Button>}
                                    {!server.alive &&
                                            <Button variant="contained" color="primary"
                                                    onClick={() => handleStart(server.id)}>
                                                Start
                                            </Button>}
                                </Stack>
                        ))}
                    </Stack>
                </Paper>
            </>
    )
}

export default AdditionalServersPage;