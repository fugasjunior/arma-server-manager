import {serversApi} from "../api/client"
import {useState} from "react";
import ServerListEntry from "../components/servers/serverListEntry/ServerListEntry.tsx";
import NewServerButton from "../components/servers/NewServerButton";
import Table from "@mui/material/Table";
import TableContainer from "@mui/material/TableContainer";
import Paper from "@mui/material/Paper";
import TableBody from "@mui/material/TableBody";
import {toast} from "react-toastify";
import ConfirmationDialog from "../UI/ConfirmationDialog";
import ServerLogs from "../components/servers/serverListEntry/ServerLogs.tsx";
import {ServerDto} from "../api/generated";
import {useServers} from "../hooks/queries/useServers";
import {useQueryClient, useQueries} from "@tanstack/react-query";
import {queryKeys} from "../api/queryKeys";

const ServersPage = () => {
    const queryClient = useQueryClient();
    const {data: servers = []} = useServers();

    const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
    const [serverToDelete, setServerToDelete] = useState<ServerDto | null>();
    const [logServerId, setLogServerId] = useState<number>();
    const [isLogOpen, setIsLogOpen] = useState(false);

    const statusQueries = useQueries({
        queries: servers.map(server => ({
            queryKey: queryKeys.serverStatus(server.id!),
            queryFn: async () => (await serversApi.getServerStatus({id: server.id!})).data,
            refetchInterval: 10000,
            enabled: server.id != null,
        })),
    });

    const isServerWithSamePortRunning = (server: ServerDto) => {
        return servers
            .filter(s => s.id !== server.id)
            .filter(s => statusQueries[servers.indexOf(s)]?.data?.alive)
            .some(s => s.port === server.port || s.queryPort === server.queryPort);
    };

    const handleStartServer = async (id: number) => {
        await serversApi.startServer({id});
        await queryClient.invalidateQueries({queryKey: queryKeys.serverStatus(id)});
    };

    const handleStopServer = async (id: number) => {
        await serversApi.stopServer({id});
        await queryClient.invalidateQueries({queryKey: queryKeys.serverStatus(id)});
    };

    const handleRestartServer = async (id: number) => {
        await serversApi.restartServer({id});
        await queryClient.invalidateQueries({queryKey: queryKeys.serverStatus(id)});
    };

    const handleDeleteServerClicked = (server: ServerDto) => {
        setServerToDelete(server);
        setDeleteDialogOpen(true);
    };

    const handleDeleteServer = async () => {
        if (!serverToDelete || !serverToDelete.id) {
            return;
        }

        const deleted = serverToDelete;
        setServerToDelete(null);
        setDeleteDialogOpen(false);

        await serversApi.deleteServer({id: deleted.id!});
        toast.success(`Server '${deleted.name}' successfully deleted`);
        await queryClient.invalidateQueries({queryKey: queryKeys.servers});
    };

    const handleDeleteDialogClose = () => {
        setDeleteDialogOpen(false);
        setServerToDelete(null);
    };

    const handleOpenLogs = (serverId: number) => {
        setIsLogOpen(true);
        setLogServerId(serverId);
    };

    const handleCloseLogs = () => {
        setIsLogOpen(false);
    };

    const handleTargetHcChanged = async () => {
        await queryClient.invalidateQueries({queryKey: queryKeys.servers});
    };

    const handleDuplicateServer = async (server: ServerDto) => {
        const duplicatedServer = {...server, name: server.name + " (copy)"};
        await serversApi.createServer({serverDto: duplicatedServer});
        toast.success(`Server '${server.name}' successfully duplicated`);
        await queryClient.invalidateQueries({queryKey: queryKeys.servers});
    };

    return (
        <>
            <NewServerButton/>
            {isLogOpen && logServerId !== undefined && <ServerLogs onClose={handleCloseLogs} serverId={logServerId}/>}
            <TableContainer component={Paper}>
                <Table>
                    <TableBody>
                        {servers.map(server =>
                            <ServerListEntry key={server.id}
                                             server={server}
                                             onStartServer={handleStartServer}
                                             onStopServer={handleStopServer}
                                             onRestartServer={handleRestartServer}
                                             onDuplicateServer={handleDuplicateServer}
                                             onOpenLogs={handleOpenLogs}
                                             onDeleteServer={() => handleDeleteServerClicked(server)}
                                             serverWithSamePortRunning={isServerWithSamePortRunning(server)}
                                             onTargetHcChanged={handleTargetHcChanged}
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
};

export default ServersPage;
