import {deleteServer, getServers, restartServer, startServer, stopServer} from "../services/serversService"
import {useEffect, useState} from "react";
import {Link} from "react-router-dom";
import {useInterval} from "../hooks/use-interval";
import ServerListEntry from "../components/servers/ServerListEntry";
import {Button, Divider, Fab, Stack} from "@mui/material";
import AddCircleOutlineIcon from '@mui/icons-material/AddCircleOutline';

const ServersPage = () => {
    const [servers, setServers] = useState([]);

    useEffect(() => {
        fetchServers()
    }, [])

    useInterval(async () => {
        fetchServers();
    }, 2000);

    const fetchServers = async () => {
        const {data: servers} = await getServers();
        setServers(servers.servers);
    }

    const isServerWithSamePortRunning = server => {
        const activeServerWithSamePort = servers.filter(s => s !== server)
        .filter(s => s.instanceInfo && s.instanceInfo.alive)
        .filter(s => s.port === server.port || s.queryPort === server.queryPort);
        if (activeServerWithSamePort[0]) {
            return activeServerWithSamePort[0];
        }
        return null;
    }

    const handleStartServer = async id => {
        const newServers = [...servers];
        const server = newServers.find(s => s.id === id);
        server.instanceInfo = {
            alive: true
        }
        setServers(newServers);
        await startServer(id);
    };

    const handleStopServer = async id => {
        const newServers = [...servers];
        const server = newServers.find(s => s.id === id);
        server.instanceInfo = {
            alive: false
        }
        setServers(newServers);
        await stopServer(id);
    };

    const handleRestartServer = async id => {
        const newServers = [...servers];
        const server = newServers.find(s => s.id === id);
        server.instanceInfo = {
            alive: true
        }
        setServers(newServers);
        await restartServer(id);
    };

    const handleDeleteServer = async id => {
        // TODO add confirmation modal
        const newServers = servers.filter(s => s.id !== id);
        setServers(newServers);
        await deleteServer(id);
    }

    return (
            <>
                <Button variant="contained" size="large" sx={{mt: 2, mb: 4}}
                component={Link} to="/servers/new"
                >
                    <AddCircleOutlineIcon sx={{mr: 1}}/>
                    Create new server
                </Button>
                <Stack spacing={4} divider={<Divider orientation="horizontal" flexItem />}>
                    {servers.map(server =>
                            <ServerListEntry key={server.id}
                                             server={server}
                                             onStartServer={handleStartServer}
                                             onStopServer={handleStopServer}
                                             onRestartServer={handleRestartServer}
                                             onDeleteServer={handleDeleteServer}
                                             serverWithSamePortRunning={isServerWithSamePortRunning(server)}
                            />
                    )}
                </Stack>
            </>
    )
}

export default ServersPage;