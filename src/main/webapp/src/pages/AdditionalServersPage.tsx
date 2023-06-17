import {useEffect, useState} from "react";
import {useInterval} from "../hooks/use-interval";
import {Avatar, Button, Divider, Stack, Typography} from "@mui/material";
import {getAdditionalServers, startAdditionalServer, stopAdditionalServer} from "../services/additionalServersService";
import Paper from "@mui/material/Paper";
import config from "../config";
import TableSkeletons from "../UI/TableSkeletons";
import CircularProgress from "@mui/material/CircularProgress";
import {AdditionalServerDto} from "../dtos/AdditionalServerDto.ts";

const AdditionalServersPage = () => {
    const [isLoading, setIsLoading] = useState(true);
    const [additionalServers, setAdditionalServers] = useState<Array<AdditionalServerDto>>([]);

    useEffect(() => {
        fetchAdditionalServers();
    }, [])

    useInterval(async () => {
        fetchAdditionalServers();
    }, 2000);

    const fetchAdditionalServers = async () => {
        const {data: servers} = await getAdditionalServers();
        setAdditionalServers(servers.servers);
        setIsLoading(false);
    }

    const handleStart = async (id: number) => {
        await startAdditionalServer(id);
        setAdditionalServers(prevState => {
            const newServers = [...prevState];
            const server = newServers.find(server => server.id === id);
            if (server) {
                server.alive = true;
            }
            return newServers;
        })
    };

    const handleStop = async (id: number) => {
        await stopAdditionalServer(id);
        setAdditionalServers(prevState => {
            const newServers = [...prevState];
            const server = newServers.find(server => server.id === id);
            if (server) {
                server.alive = false;
                server.startedAt = null;
            }
            return newServers;
        })
    };

    return (
        <>
            {!isLoading && additionalServers.length === 0 &&
                <div>
                    <Typography variant="h5" align="center">No additional servers set up</Typography>
                    <Typography variant="body1" align="center">You can set them up manually through
                        database</Typography>
                </div>
            }
            {!isLoading && additionalServers.length > 0 && <Paper>
                <TableSkeletons count={8} spacing={6} display={isLoading}/>
                {!isLoading && <Stack p={3} spacing={2} divider={<Divider orientation="horizontal" flexItem/>}>
                    {additionalServers.map(server => (
                        <Stack key={server.id} direction="row" justifyContent="space-between" width="100%">
                            <Stack direction="row" spacing={3}>
                                <Avatar src={server.imageUrl} alt={`${server.name} icon`}/>
                                <p>{server.name}</p>
                            </Stack>
                            {server.startedAt &&
                                <div>
                                    Started at: {new Date(server.startedAt).toLocaleString(undefined,
                                    config.dateFormat)}
                                </div>
                            }
                            {server.startedAt && server.alive &&
                                <Button variant="contained" color="error"
                                        onClick={() => handleStop(server.id)}>
                                    Stop
                                </Button>}
                            {!server.startedAt &&
                                <Button variant="contained" color="primary"
                                        disabled={!server.startedAt && server.alive}
                                        onClick={() => handleStart(server.id)}>
                                    {!server.startedAt && server.alive ?
                                        <CircularProgress size={24}/> : "Start"}
                                </Button>}
                        </Stack>
                    ))}
                </Stack>
                }
            </Paper>
            }
        </>
    )
}

export default AdditionalServersPage;