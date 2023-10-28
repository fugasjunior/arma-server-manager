import {
    deleteServer,
    getServers,
    getServerStatus,
    restartServer,
    startServer,
    stopServer
} from "../services/serversService"
import {useEffect, useState} from "react";
import {useInterval} from "../hooks/use-interval";
import ServerListEntry from "../components/servers/ServerListEntry";
import NewServerButton from "../components/servers/NewServerButton";
import Table from "@mui/material/Table";
import TableContainer from "@mui/material/TableContainer";
import Paper from "@mui/material/Paper";
import TableBody from "@mui/material/TableBody";
import {toast} from "material-react-toastify";
import ConfirmationDialog from "../UI/ConfirmationDialog";
import ServerLogs from "../components/servers/ServerLogs";
import {ServerDto} from "../dtos/ServerDto";

type ServerInstance = {
    server: ServerDto,
    status: ServerInstanceInfoDto | null
}

const ServersPage = () => {
    const [serverInstances, setServerInstances] = useState<ServerInstance[]>([]);
    const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
    const [serverToDelete, setServerToDelete] = useState<ServerDto | null>();
    const [logServerId, setLogServerId] = useState<number>();
    const [isLogOpen, setIsLogOpen] = useState(false);

    useEffect(() => {
        fetchServers()
    }, [])

    useInterval(async () => {
        await updateActiveServersStatus();
    }, 10000);

    const fetchServers = async () => {
        const {data: servers} = await getServers();
        const instances = servers.servers.map((server: ServerDto) => {return {server, status: null}});
        for (const instance of instances) {
            const {data: status} = await getServerStatus(instance.server.id);
            instance.status = status;
        }
        setServerInstances(instances);
    }

    function shouldUpdateServerStatus(instance: ServerInstance) {
        return instance.status === null || instance.status.alive;
    }

    const updateActiveServersStatus = async () => {
        serverInstances.filter(shouldUpdateServerStatus)
            .map(s => s.server)
            .forEach(updateServerStatus);
    }

    const updateServerStatus = async (server: ServerDto) => {
        if (server.id == null) {
            return;
        }
        const {data: instanceInfo} = await getServerStatus(server.id);
        const newInstances = [...serverInstances];
        const foundInstance = newInstances.find(instance => instance.server.id === server.id);
        if (!foundInstance) {
            return;
        }
        foundInstance.status = instanceInfo;
        setServerInstances(newInstances);
    }

    const isServerWithSamePortRunning = (server: ServerDto) => {
        const activeServerWithSamePort = serverInstances.map(instance => instance.server)
            .filter(anotherServer => anotherServer !== server)
            .filter(anotherServer => anotherServer.instanceInfo && anotherServer.instanceInfo.alive)
            .filter(anotherServer => anotherServer.port === server.port || anotherServer.queryPort === server.queryPort);
        return !!activeServerWithSamePort[0];
    }

    const handleStartServer = async (id: number) => {
        updateServerList(id, true);
        await startServer(id);
    };

    const handleStopServer = async (id: number) => {
        updateServerList(id, false);
        await stopServer(id);
    };

    const handleRestartServer = async (id: number) => {
        updateServerList(id, false);
        await restartServer(id);
    };

    const updateServerList = (targetServerId: number, isNewServerAlive: boolean): void => {
        const newInstances = [...serverInstances];
        const instance = newInstances.find(instance => instance.server.id === targetServerId);
        if (!instance) {
            return;
        }

        instance.status = {
            description: "", map: "", maxPlayers: 0, playersOnline: 0, startedAt: "", version: "",
            ...instance.status,
            alive: isNewServerAlive
        };

        setServerInstances(newInstances);
    }

    const handleDeleteServerClicked = (server: ServerDto) => {
        setServerToDelete(server);
        setDeleteDialogOpen(true);
    }

    const handleDeleteServer = async () => {
        if (!serverToDelete || !serverToDelete.id) {
            return;
        }

        setServerInstances(prevState => [...prevState].filter(server => server.server.id !== serverToDelete.id));
        await deleteServer(serverToDelete.id);
        toast.success(`Server '${serverToDelete.name}' successfully deleted`);
        setServerToDelete(null);
        setDeleteDialogOpen(false);
    }

    const handleDeleteDialogClose = () => {
        setDeleteDialogOpen(false);
        setServerToDelete(null);
    }

    const handleOpenLogs = (serverId: number) => {
        setIsLogOpen(true);
        setLogServerId(serverId);
    }

    const handleCloseLogs = () => {
        setIsLogOpen(false);
    }

    return (
        <>
            <NewServerButton/>
            {isLogOpen && logServerId !== undefined && <ServerLogs onClose={handleCloseLogs} serverId={logServerId}/>}
            <TableContainer component={Paper}>
                <Table>
                    <TableBody>
                        {serverInstances.map(instance =>
                            <ServerListEntry key={instance.server.id}
                                             server={instance.server}
                                             status={instance.status}
                                             onStartServer={handleStartServer}
                                             onStopServer={handleStopServer}
                                             onRestartServer={handleRestartServer}
                                             onOpenLogs={handleOpenLogs}
                                             onDeleteServer={() => handleDeleteServerClicked(instance.server)}
                                             serverWithSamePortRunning={isServerWithSamePortRunning(instance.server)}
                            />
                        )}
                    </TableBody>
                </Table>
            </TableContainer>
            {serverToDelete && <ConfirmationDialog
                open={deleteDialogOpen} title={`Delete server '${serverToDelete.name}'?`}
                description={"Deleting the server will cause all of its configuration to be permanently lost."}
                onConfirm={handleDeleteServer} onClose={handleDeleteDialogClose} actionLabel="Delete"
            />}
        </>
    );
}

export default ServersPage;