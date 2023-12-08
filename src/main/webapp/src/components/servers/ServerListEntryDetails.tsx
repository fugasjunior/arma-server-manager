import {ServerDto} from "../../dtos/ServerDto.ts";
import {Button, Stack} from "@mui/material";
import ModEditButton from "./ModEditButton.tsx";
import ListBuilderDLCsEdit from "./ListBuilderDLCsEdit.tsx";
import TextSnippetIcon from "@mui/icons-material/TextSnippet";
import AutomaticRestartSettings from "./AutomaticRestartSettings.tsx";

export function ServerListEntryDetails(props: {
    server: ServerDto,
    serverStatus: ServerInstanceInfoDto | null,
    onClick: () => void
}) {
    return <Stack spacing={2}>
        <Stack direction="row" spacing={2}>
            <ModEditButton server={props.server} serverStatus={props.serverStatus}/>
            {props.server.type === "ARMA3" &&
                <ListBuilderDLCsEdit status={props.serverStatus} server={props.server}/>}
            <Button
                variant="contained"
                startIcon={<TextSnippetIcon/>} onClick={props.onClick}
                color="info">
                Logs
            </Button>
        </Stack>
        <Stack direction="row">
            <AutomaticRestartSettings serverId={props.server.id!} dto={props.server.automaticRestart}/>
        </Stack>
    </Stack>;
}
