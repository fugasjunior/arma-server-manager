import TableRow from "@mui/material/TableRow";
import TableCell from "@mui/material/TableCell";
import {Stack} from "@mui/material";
import {ServerDto} from "../../../api/generated";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import ExpandLessIcon from "@mui/icons-material/ExpandLess";
import {useState} from "react";
import IconButton from "@mui/material/IconButton";
import {ServerListEntryDetails} from "./ServerListEntryDetails.tsx";
import {ServerStatusDetails} from "./ServerStatusDetails.tsx";
import {ServerHeader} from "./ServerHeader.tsx";
import {SeverControls} from "./SeverControls.tsx";
import {ServerActionsMenu} from "./ServerActionsMenu.tsx";
import {useServerStatus} from "../../../hooks/queries/useServerStatus.ts";
import PermissionGuard from "../../auth/PermissionGuard";

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
    onToggleAutoStart?: (id: number, enabled: boolean) => void,
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
                <TableCell colSpan={5} sx={{position: "relative"}}>
                    <Stack direction="row" spacing={2} sx={{alignItems: "center", display: "flex", flex: 1, minWidth: 0}}>
                        <ServerHeader server={server}/>
                        <PermissionGuard permission="SERVER_OPERATE">
                            <SeverControls
                                serverRunning={status?.alive ?? null} server={server}
                                onStartServer={() => onStartServer(server.id as number)}
                                onStopServer={() => onStopServer(server.id as number)}
                                onRestartServer={() => onRestartServer(server.id as number)}
                                disabled={serverWithSamePortRunning}
                            />
                        </PermissionGuard>
                        <Stack direction="row" spacing={1} sx={{position: "absolute", right: 8, alignItems: "center"}}>
                            {serverRunning && status && <ServerStatusDetails status={status}/>}
                            <ServerActionsMenu
                                server={server}
                                onDuplicateServer={onDuplicateServer}
                                onDeleteServer={onDeleteServer}
                            />
                            <IconButton data-testid={`server-${server.id}-expand-btn`} onClick={handleExpandClick}>
                                {isExpanded ? <ExpandLessIcon/> : <ExpandMoreIcon/>}
                            </IconButton>
                        </Stack>
                    </Stack>
                </TableCell>
            </TableRow>
            {isExpanded && <TableRow>
                <TableCell colSpan={5}>
                    <ServerListEntryDetails server={server} serverStatus={status}
                                            onClick={() => props.onOpenLogs(server.id as number)}
                                            onTargetHcChanged={props.onTargetHcChanged}
                                            onToggleAutoStart={props.onToggleAutoStart}
                    />
                </TableCell>
            </TableRow>}
        </>
    );
};

export default ServerListEntry;
