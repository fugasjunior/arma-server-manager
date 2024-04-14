import {Link} from "react-router-dom";
import {Button} from "@mui/material";
import DeleteIcon from '@mui/icons-material/Delete';
import SettingsIcon from '@mui/icons-material/Settings';
import TableRow from "@mui/material/TableRow";
import TableCell from "@mui/material/TableCell";
import {ServerDto} from "../../../dtos/ServerDto.ts";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import ExpandLessIcon from "@mui/icons-material/ExpandLess";
import {useState} from "react";
import IconButton from "@mui/material/IconButton";
import {ServerListEntryDetails} from "./ServerListEntryDetails.tsx";
import {ServerStatusDetails} from "./ServerStatusDetails.tsx";
import {ServerHeader} from "./ServerHeader.tsx";
import {SeverControls} from "./SeverControls.tsx";

type ServerListEntryProps = {
    server: ServerDto,
    status: ServerInstanceInfoDto | null,
    serverWithSamePortRunning: boolean
    onStartServer: (id: number) => void,
    onStopServer: (id: number) => void,
    onRestartServer: (id: number) => void,
    onDeleteServer: (id: number) => void,
    onOpenLogs: (id: number) => void
}

const ServerListEntry = (props: ServerListEntryProps) => {
    const {
        server,
        status,
        onStartServer,
        onStopServer,
        onRestartServer,
        onDeleteServer,
        serverWithSamePortRunning
    } = props;

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
                    <SeverControls
                        serverRunning={status && status.alive} server={server}
                        onStartServer={() => onStartServer(server.id as number)}
                        onStopServer={() => onStopServer(server.id as number)}
                        onRestartServer={() => onRestartServer(server.id as number)}
                        disabled={serverWithSamePortRunning}
                    />
                </TableCell>
                <TableCell>
                    <Button id={`server-${server.id}-settings-btn`}
                            className="server-settings-btn"
                            component={Link} to={"/servers/" + server.id}
                            variant="outlined" startIcon={<SettingsIcon/>}
                    >
                        Settings
                    </Button>
                </TableCell>
                <TableCell>
                    {serverRunning && status &&
                        <ServerStatusDetails status={status}/>
                    }
                    {!serverRunning &&
                        <Button id={`server-${server.id}-delete-btn`}
                                className="server-delete-btn"
                                variant="outlined" startIcon={<DeleteIcon/>} color="error"
                                onClick={() => onDeleteServer(server.id as number)}>Delete
                        </Button>}
                </TableCell>
                <TableCell>
                    <IconButton onClick={handleExpandClick}>
                        {isExpanded ? <ExpandLessIcon/> : <ExpandMoreIcon/>}
                    </IconButton>
                </TableCell>
            </TableRow>
            {isExpanded && <TableRow>
                <TableCell colSpan={5}>
                    <ServerListEntryDetails server={server} serverStatus={status}
                                            onClick={() => props.onOpenLogs(server.id as number)}/>
                </TableCell>
            </TableRow>}
        </>
    );
};

export default ServerListEntry;
