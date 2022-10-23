import {Link} from "react-router-dom";
import {Avatar, Button, Divider, Grid, List, ListItem, ListItemAvatar, ListItemText, Stack} from "@mui/material";
import DeleteIcon from '@mui/icons-material/Delete';
import PlayCircleFilledIcon from '@mui/icons-material/PlayCircleFilled';
import SettingsIcon from '@mui/icons-material/Settings';
import DirectionsRunIcon from '@mui/icons-material/DirectionsRun';
import MapIcon from '@mui/icons-material/Map';

const ServerListEntry = (props) => {
    const {server, onStartServer, onStopServer, onRestartServer, onDeleteServer, serverWithSamePortRunning} = props;

    const serverRunning = server.instanceInfo && server.instanceInfo.alive;

    return (
            <>
                <Grid key={server.id} spacing={0} alignItems="center" container>
                    <Grid item xs={1} sx={{display: {xs: 'none', sm: 'none', md: 'block'}}}>
                        <Avatar src="./img/arma3_icon.png" alt="Arma 3 icon"/>
                    </Grid>
                    <Grid item xs={4}>
                        <p>{server.name}</p>
                        <p>Port: {server.port}</p>
                        {server.description && <p>{server.description}</p>}
                        {server.activeMods.length > 0 && <p>Mods active</p>}
                        {server.activeDLCs.length > 0 && <p>Creator DLC(s) active</p>}
                    </Grid>
                    <Grid item xs={6} md={4}>
                        <Stack direction="row" spacing={1}>
                            {serverRunning ?
                                    <>
                                        <Button variant="contained" color="error"
                                                onClick={() => onStopServer(server.id)}>
                                            Stop
                                        </Button>
                                        <Button onClick={() => onRestartServer(server.id)}>Restart
                                        </Button>
                                    </>
                                    : <Button variant="contained" startIcon={<PlayCircleFilledIcon/>}
                                              disabled={serverWithSamePortRunning}
                                              onClick={() => onStartServer(server.id)}>
                                        Start
                                    </Button>
                            }
                            <Button component={Link} to={"/servers/" + server.id}
                                    variant="outlined" startIcon={<SettingsIcon/>}
                            >
                                Settings
                            </Button>
                        </Stack>
                    </Grid>
                    <Grid item xs={2}>
                        {serverRunning && server.instanceInfo.maxPlayers &&
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
                                <Button variant="outlined" startIcon={<DeleteIcon/>} color="error"
                                        onClick={() => onDeleteServer(server.id)}>Delete
                                </Button>}
                    </Grid>
                </Grid>
                <Divider orientation="vertical"/>
            </>
    )
            ;
};

export default ServerListEntry;