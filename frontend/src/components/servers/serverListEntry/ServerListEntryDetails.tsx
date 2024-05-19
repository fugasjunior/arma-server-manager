import {ServerDto} from "../../../dtos/ServerDto.ts";
import {Button, Stack} from "@mui/material";
import ModEditButton from "../ModEditButton.tsx";
import ListBuilderDLCsEdit from "../ListBuilderDLCsEdit.tsx";
import TextSnippetIcon from "@mui/icons-material/TextSnippet";
import AutomaticRestartSettings from "./AutomaticRestartSettings.tsx";
import {HeadlessClientControls} from "./HeadlessClientControls.tsx";

export function ServerListEntryDetails(props: {
    server: ServerDto,
    serverStatus: ServerInstanceInfoDto | null,
    onClick: () => void,
    onDuplicateServer: (server: ServerDto) => void
}) {
    return <Stack direction="row" spacing={2} justifyItems="center" alignItems="center">
        <ModEditButton server={props.server} serverStatus={props.serverStatus}/>
        {props.server.type === "ARMA3" &&
            <ListBuilderDLCsEdit status={props.serverStatus} server={props.server}/>}
        <Button
            variant="contained"
            startIcon={<TextSnippetIcon/>} onClick={props.onClick}
            color="info">
            Logs
        </Button>

        <Button
            variant="contained"
            startIcon={<TextSnippetIcon/>} onClick={() => props.onDuplicateServer(props.server)}
            color="info">
            Duplicate
        </Button>

        <AutomaticRestartSettings serverId={props.server.id!} dto={props.server.automaticRestart}/>

        {props.serverStatus?.alive && props.server.type === "ARMA3" &&
            <HeadlessClientControls serverId={props.server.id!} serverStatus={props.serverStatus}/>}
    </Stack>;
}
