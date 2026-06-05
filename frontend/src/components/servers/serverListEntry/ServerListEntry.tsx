import {Link} from "react-router-dom";
import {Button} from "@mui/material";
import PermissionGuard from "../../auth/PermissionGuard";
import DeleteIcon from '@mui/icons-material/Delete';
import SettingsIcon from '@mui/icons-material/Settings';
import TableRow from "@mui/material/TableRow";
import TableCell from "@mui/material/TableCell";
import {ServerDto} from "../../../api/generated";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import ExpandLessIcon from "@mui/icons-material/ExpandLess";
import {useState} from "react";
import IconButton from "@mui/material/IconButton";
import {ServerListEntryDetails} from "./ServerListEntryDetails.tsx";
import {ServerStatusDetails} from "./ServerStatusDetails.tsx";
import {ServerHeader} from "./ServerHeader.tsx";
import {SeverControls} from "./SeverControls.tsx";
import {useServerStatus} from "../../../hooks/queries/useServerStatus.ts";

type ServerListEntryProps = {
    server: ServerDto,
    serverWithSamePortRunning: boolean,
    onStartServer: (id: number) => void,
    onStopServer: (id: number) => void,
    onRestartServer: (id: number) => void,
    onDeleteServer: (id: number) => void,
    onOpenLogs: (id: number) => void,
    onDuplicateServer: (server: ServerDto) => void,
    onTargetHcChanged?: () => void,
}

const ServerListEntry = (props: ServerListEntryProps) => {
    const {
        server,
        onStartServer,
        onStopServer,
        onRestartServer,
        onDeleteServer,
        onDuplicateServer,
        serverWithSamePortRunning,
    } = props;

    const {data: status = null} = useServerStatus(server.id);
    const [isExpanded, setExpanded] = useState(false);

    if (server.id === undefined) {
        console.error("Server should have ID assigned.");
        return;
    }

    const serverRunning = status && status.alive;

    const handleExpandClick = () => {
        setExpanded(prevState => !prevState);
    };

    return (
        <>
            <TableRow id={`server-${server.id}-list-entry`} className="server-list-entry">
                <TableCell>
                    <ServerHeader server={server}/>
                </TableCell>
                <TableCell>
                    <PermissionGuard permission="SERVER_OPERATE">
                        <SeverControls
                            serverRunning={status?.alive ?? null} server={server}
                            onStartServer={() => onStartServer(server.id as number)}
                            onStopServer={() => onStopServer(server.id as number)}
                            onRestartServer={() => onRestartServer(server.id as number)}
                            disabled={serverWithSamePortRunning}
                        />
                    </PermissionGuard>
                </TableCell>
                <TableCell>
                    <PermissionGuard permission="SERVER_VIEW">
                        <Button id={`server-${server.id}-settings-btn`}
                                className="server-settings-btn"
                                component={Link} to={"/servers/" + server.id}
                                variant="outlined" startIcon={<SettingsIcon/>}
                        >
                            Settings
                        </Button>
                    </PermissionGuard>
                </TableCell>
                <TableCell>
                    {serverRunning && status &&
                        <ServerStatusDetails status={status}/>
                    }
                    {!serverRunning &&
                        <PermissionGuard permission="SERVER_DELETE">
                            <Button id={`server-${server.id}-delete-btn`}
                                    className="server-delete-btn"
                                    variant="outlined" startIcon={<DeleteIcon/>} color="error"
                                    onClick={() => onDeleteServer(server.id as number)}>Delete
                            </Button>
                        </PermissionGuard>}
                </TableCell>
                <TableCell>
                    <IconButton data-testid={`server-${server.id}-expand-btn`} onClick={handleExpandClick}>
                        {isExpanded ? <ExpandLessIcon/> : <ExpandMoreIcon/>}
                    </IconButton>
                </TableCell>
            </TableRow>
            {isExpanded && <TableRow>
                <TableCell colSpan={5}>
                    <ServerListEntryDetails server={server} serverStatus={status} onDuplicateServer={onDuplicateServer}
                                            onClick={() => props.onOpenLogs(server.id as number)}
                                            onTargetHcChanged={props.onTargetHcChanged}
                    />
                </TableCell>
            </TableRow>}
        </>
    );
};

export default ServerListEntry;
