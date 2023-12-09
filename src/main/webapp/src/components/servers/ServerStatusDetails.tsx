import {Avatar, List, ListItem, ListItemAvatar, ListItemText} from "@mui/material";
import DirectionsRunIcon from "@mui/icons-material/DirectionsRun";
import MapIcon from "@mui/icons-material/Map";

export function ServerStatusDetails(props: { status: ServerInstanceInfoDto }) {
    return <List>
        <ListItem>
            <ListItemAvatar>
                <Avatar>
                    <DirectionsRunIcon/>
                </Avatar>
            </ListItemAvatar>
            <ListItemText primary="Players"
                          secondary={`${props.status.playersOnline} / ${props.status.maxPlayers}`}/>
        </ListItem>
        {props.status.map &&
            <ListItem>
                <ListItemAvatar>
                    <Avatar>
                        <MapIcon/>
                    </Avatar>
                </ListItemAvatar>
                <ListItemText primary="Map"
                              secondary={`${props.status.map}`}/>
            </ListItem>
        }
    </List>;
}
