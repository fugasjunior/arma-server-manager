import {ServerDto, ServerType} from "../../../api/generated";
import {Stack, Typography} from "@mui/material";
import {ServerIcon} from "./ServerIcon.tsx";
import SERVER_NAMES from "../../../util/serverNames.ts";

export function ServerHeader(props: { server: ServerDto }) {
    return <Stack direction="row" spacing={2} sx={{alignItems: "center"}}>
        <ServerIcon serverType={props.server.type}/>
        <Stack>
            <Typography variant="h6">{props.server.name}</Typography>
            <Typography variant="subtitle2">
                {SERVER_NAMES.get(props.server.type as ServerType)}
            </Typography>
            {props.server.description && <Typography variant="body2">{props.server.description}</Typography>}
            <Typography variant="body2">Port: {props.server.port}</Typography>
        </Stack>
    </Stack>;
}
