import {ServerDto} from "../../dtos/ServerDto.ts";
import {Stack, Typography} from "@mui/material";
import {ServerIcon} from "./ServerIcon.tsx";

export function ServerHeader(props: { server: ServerDto }) {
    return <Stack direction="row" spacing={2} alignItems="center">
        <ServerIcon serverType={props.server.type}/>
        <Stack>
            <Typography variant="h6">{props.server.name}</Typography>
            {props.server.description && <Typography variant="body2">{props.server.description}</Typography>}
            <Typography variant="body2">Port: {props.server.port}</Typography>
        </Stack>
    </Stack>;
}
