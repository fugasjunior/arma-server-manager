import {Arma3ServerDto, ServerDto} from "../../dtos/ServerDto.ts";
import {Stack} from "@mui/material";
import {ServerIcon} from "./ServerIcon.tsx";

export function ServerHeader(props: { server: ServerDto }) {
    return <Stack direction="row" spacing={2}>
        <ServerIcon serverType={props.server.type}/>

        <Stack>
        <p>{props.server.name}</p>
        <p>Port: {props.server.port}</p>
        {props.server.description && <p>{props.server.description}</p>}
        {props.server.type === "ARMA3" && <>
            {(props.server as Arma3ServerDto).activeMods.length > 0 && <p>Mods active</p>}
            {(props.server as Arma3ServerDto).activeDLCs.length > 0 && <p>Creator DLC(s) active</p>}
        </>}
        </Stack>
    </Stack>;
}
