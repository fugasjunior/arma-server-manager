import {ServerType} from "../../dtos/ServerDto.ts";
import arma3Icon from "../../img/arma3_icon.png";
import dayZIcon from "../../img/dayz_icon.png";
import reforgerIcon from "../../img/reforger_icon.png";
import {Avatar} from "@mui/material";

const SERVER_ICON_URLS = new Map<ServerType, string>([
    [ServerType.ARMA3, arma3Icon],
    [ServerType.DAYZ, dayZIcon],
    [ServerType.DAYZ_EXP, dayZIcon],
    [ServerType.REFORGER, reforgerIcon]
]);

export function ServerIcon(props: { serverType: string}) {
    return <Avatar src={SERVER_ICON_URLS.get(ServerType[props.serverType as keyof typeof ServerType])}
                   alt={`${props.serverType} icon`}/>;
}
