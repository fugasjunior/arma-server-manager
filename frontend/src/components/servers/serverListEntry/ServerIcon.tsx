import {ServerType} from "../../../api/generated";
import arma3Icon from "../../../img/arma3_icon.png";
import dayZIcon from "../../../img/dayz_icon.png";
import reforgerIcon from "../../../img/reforger_icon.png";
import {Avatar} from "@mui/material";

const SERVER_ICON_URLS = new Map<ServerType, string>([
    [ServerType.Arma3, arma3Icon],
    [ServerType.Dayz, dayZIcon],
    [ServerType.DayzExp, dayZIcon],
    [ServerType.Reforger, reforgerIcon]
]);

export function ServerIcon(props: { serverType: string}) {
    return <Avatar src={SERVER_ICON_URLS.get(props.serverType as ServerType)}
                   alt={`${props.serverType} icon`}/>;
}
