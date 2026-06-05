import {ServerDto, ServerInstanceInfoDto, ServerType} from "../../../api/generated";
import {Arma3ServerDto} from "../../../api/serverModels.ts";
import {Button, Stack} from "@mui/material";
import ModEditButton from "../ModEditButton.tsx";
import ListBuilderDLCsEdit from "../ListBuilderDLCsEdit.tsx";
import Arma3ScenariosControl from "../Arma3ScenariosControl.tsx";
import Arma3BikeysControl from "../Arma3BikeysControl.tsx";
import TextSnippetIcon from "@mui/icons-material/TextSnippet";
import AutomaticRestartSettings from "./AutomaticRestartSettings.tsx";
import {HeadlessClientControls} from "./HeadlessClientControls.tsx";
import ContentCopyIcon from '@mui/icons-material/ContentCopy';
import PermissionGuard from "../../auth/PermissionGuard.tsx";

export function ServerListEntryDetails(props: {
    server: ServerDto,
    serverStatus: ServerInstanceInfoDto | null,
    onClick: () => void,
    onDuplicateServer: (server: ServerDto) => void,
    onTargetHcChanged?: () => void,
}) {
    return <Stack direction="row">
        <Stack direction="row" spacing={2}
               sx={{flexGrow: 1, justifyItems: "center", alignItems: "center", justifyContent: "flex-start"}}>
            <PermissionGuard permission="MOD_VIEW">
                <ModEditButton server={props.server} serverStatus={props.serverStatus}/>
            </PermissionGuard>
            {props.server.type === ServerType.Arma3 &&
                <PermissionGuard permission="MOD_MODIFY">
                    <ListBuilderDLCsEdit status={props.serverStatus} server={props.server}/>
                </PermissionGuard>}
            {props.server.type === ServerType.Arma3 &&
                <PermissionGuard permission="SCENARIO_VIEW">
                    <Arma3ScenariosControl server={props.server} status={props.serverStatus}/>
                </PermissionGuard>}
            {props.server.type === ServerType.Arma3 &&
                <PermissionGuard permission="BIKEY_VIEW">
                    <Arma3BikeysControl server={props.server} status={props.serverStatus}/>
                </PermissionGuard>}
            <PermissionGuard permission="SERVER_LOGS_VIEW">
                <Button
                    variant="contained"
                    startIcon={<TextSnippetIcon/>} onClick={props.onClick}
                    color="info">
                    Logs
                </Button>
            </PermissionGuard>

            {props.server.automaticRestart &&
                <AutomaticRestartSettings serverId={props.server.id!} dto={props.server.automaticRestart}/>}

            {props.server.type === ServerType.Arma3 &&
                <HeadlessClientControls
                    serverId={props.server.id!}
                    serverStatus={props.serverStatus}
                    targetCount={(props.server as Arma3ServerDto).targetHeadlessClientsCount ?? 0}
                    onTargetChanged={props.onTargetHcChanged}
                />}
        </Stack>
        <PermissionGuard permission="SERVER_MODIFY">
            <Stack direction="row" sx={{flexGrow: 0}}>
                <Button
                    variant="outlined"
                    size="small"
                    startIcon={<ContentCopyIcon/>} onClick={() => props.onDuplicateServer(props.server)}
                    color="info">
                    Duplicate
                </Button>
            </Stack>
        </PermissionGuard>
    </Stack>;
}
