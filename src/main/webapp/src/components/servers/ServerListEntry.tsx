import {Link} from "react-router-dom";
import {Avatar, Button, List, ListItem, ListItemAvatar, ListItemText, Stack} from "@mui/material";
import DeleteIcon from '@mui/icons-material/Delete';
import PlayCircleFilledIcon from '@mui/icons-material/PlayCircleFilled';
import SettingsIcon from '@mui/icons-material/Settings';
import DirectionsRunIcon from '@mui/icons-material/DirectionsRun';
import MapIcon from '@mui/icons-material/Map';
import TableRow from "@mui/material/TableRow";
import TableCell from "@mui/material/TableCell";
import arma3Icon from "../../img/arma3_icon.png";
import dayZIcon from "../../img/dayz_icon.png";
import reforgerIcon from "../../img/reforger_icon.png";

import ModEditButton from "./ModEditButton";
import ListBuilderDLCsEdit from "./ListBuilderDLCsEdit";
import TextSnippetIcon from '@mui/icons-material/TextSnippet';
import {Arma3ServerDto, ServerDto, ServerType} from "../../dtos/ServerDto.ts";

const SERVER_ICON_URLS = new Map<ServerType, string>([
    [ServerType.ARMA3, arma3Icon],
    [ServerType.DAYZ, dayZIcon],
    [ServerType.DAYZ_EXP, dayZIcon],
    [ServerType.REFORGER, reforgerIcon]
]);

type ServerListEntryProps = {
    server: ServerDto,
    serverWithSamePortRunning: boolean
    onStartServer: (id: number) => void,
    onStopServer: (id: number) => void,
    onRestartServer: (id: number) => void,
    onDeleteServer: (id: number) => void,
    onOpenLogs: (id: number) => void
}

const ServerListEntry = (props: ServerListEntryProps) => {
    const {server, onStartServer, onStopServer, onRestartServer, onDeleteServer, serverWithSamePortRunning} = props;
    if (server.id === undefined) {
        console.log("Server should have ID assigned.");
        return;
    }

    const serverRunning = server.instanceInfo && server.instanceInfo.alive;

    return (
        <TableRow id={`server-${server.id}-list-entry`} className="server-list-entry">
            <TableCell>
                <Avatar src={SERVER_ICON_URLS.get(ServerType[server.type as keyof typeof ServerType])}
                        alt={`${server.type} icon`}/>
            </TableCell>
            <TableCell>
                <Stack>
                    <p>{server.name}</p>
                    <p>Port: {server.port}</p>
                    {server.description && <p>{server.description}</p>}
                    {server.type === "ARMA3" && <>
                        {(server as Arma3ServerDto).activeMods.length > 0 && <p>Mods active</p>}
                        {(server as Arma3ServerDto).activeDLCs.length > 0 && <p>Creator DLC(s) active</p>}
                    </>}
                </Stack>
            </TableCell>
            <TableCell>
                <Stack direction="row" spacing={1}>
                    {serverRunning ?
                        <>
                            <Button id={`server-${server.id}-stop-btn`}
                                    className="server-stop-btn"
                                    variant="contained" color="error"
                                    onClick={() => onStopServer(server.id as number)}>
                                Stop
                            </Button>
                            <Button onClick={() => onRestartServer(server.id as number)}>Restart
                            </Button>
                        </>
                        : <Button id={`server-${server.id}-start-btn`}
                                  variant="contained" startIcon={<PlayCircleFilledIcon/>}
                                  disabled={serverWithSamePortRunning}
                                  onClick={() => onStartServer(server.id as number)}>
                            Start
                        </Button>
                    }
                </Stack>
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
                <ModEditButton server={server}/>
            </TableCell>
            <TableCell>
                {server.type === "ARMA3" && <ListBuilderDLCsEdit server={server}/>}
            </TableCell>
            <TableCell>
                <Button startIcon={<TextSnippetIcon/>} onClick={() => props.onOpenLogs(server.id as number)}
                        color="info">
                    Logs
                </Button>
            </TableCell>
            <TableCell>
                {serverRunning && server.instanceInfo &&
                    <List>
                        <ListItem>
                            <ListItemAvatar>
                                <Avatar>
                                    <DirectionsRunIcon/>
                                </Avatar>
                            </ListItemAvatar>
                            <ListItemText primary="Players"
                                          secondary={`${server.instanceInfo.playersOnline} / ${server.instanceInfo.maxPlayers}`}/>
                        </ListItem>
                        {server.instanceInfo.map &&
                            <ListItem>
                                <ListItemAvatar>
                                    <Avatar>
                                        <MapIcon/>
                                    </Avatar>
                                </ListItemAvatar>
                                <ListItemText primary="Map"
                                              secondary={`${server.instanceInfo.map}`}/>
                            </ListItem>
                        }
                    </List>
                }
                {!serverRunning &&
                    <Button id={`server-${server.id}-delete-btn`}
                            className="server-delete-btn"
                            variant="outlined" startIcon={<DeleteIcon/>} color="error"
                            onClick={() => onDeleteServer(server.id as number)}>Delete
                    </Button>}
            </TableCell>
        </TableRow>
    );
};

export default ServerListEntry;