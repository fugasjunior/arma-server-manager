import {ServerDto, ServerInstanceInfoDto, ServerType} from "../../../api/generated";
import {Arma3ServerDto} from "../../../api/serverModels.ts";
import {Button, Checkbox, Divider, FormControlLabel, Stack, Typography} from "@mui/material";
import {Link} from "react-router-dom";
import ModEditButton from "../ModEditButton.tsx";
import ListBuilderDLCsEdit from "../ListBuilderDLCsEdit.tsx";
import Arma3ScenariosControl from "../Arma3ScenariosControl.tsx";
import Arma3BikeysControl from "../Arma3BikeysControl.tsx";
import TextSnippetIcon from "@mui/icons-material/TextSnippet";
import SettingsIcon from "@mui/icons-material/Settings";
import AutomaticRestartSettings from "./AutomaticRestartSettings.tsx";
import {HeadlessClientControls} from "./HeadlessClientControls.tsx";
import PermissionGuard from "../../auth/PermissionGuard.tsx";

export function ServerListEntryDetails(props: {
    server: ServerDto,
    serverStatus: ServerInstanceInfoDto | null,
    onClick: () => void,
    onTargetHcChanged?: () => void,
    onToggleAutoStart?: (id: number, enabled: boolean) => void,
}) {
    return (
        <Stack
            direction={{xs: "column", md: "row"}}
            divider={<Divider orientation="vertical" flexItem/>}
            spacing={2}
            sx={{px: 1, py: 1}}
        >
            <Stack spacing={1} sx={{flex: 1, minWidth: 0}}>
                <Typography variant="overline" sx={{lineHeight: 1}}>Configuration</Typography>
                <Stack direction="row" sx={{flexWrap: "wrap", gap: 2, alignItems: "center"}}>
                    <PermissionGuard permission="SERVER_VIEW">
                        <Button
                            id={`server-${props.server.id}-settings-btn`}
                            variant="outlined"
                            startIcon={<SettingsIcon/>}
                            component={Link}
                            to={"/servers/" + props.server.id}
                        >
                            Settings
                        </Button>
                    </PermissionGuard>
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
                </Stack>
            </Stack>
            <Stack spacing={1} sx={{flex: 1, minWidth: 0}}>
                <Typography variant="overline" sx={{lineHeight: 1}}>Operations</Typography>
                <Stack direction="row" sx={{flexWrap: "wrap", gap: 2, alignItems: "center"}}>
                    <PermissionGuard permission="SERVER_LOGS_VIEW">
                        <Button variant="outlined" startIcon={<TextSnippetIcon/>} onClick={props.onClick} color="info">
                            Logs
                        </Button>
                    </PermissionGuard>
                    <PermissionGuard permission="SERVER_MODIFY">
                        <FormControlLabel
                            label="Auto start"
                            control={
                                <Checkbox
                                    checked={!!props.server.autoStart}
                                    onChange={(e) => props.onToggleAutoStart?.(props.server.id as number, e.target.checked)}
                                    size="small"
                                />
                            }
                        />
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
            </Stack>
        </Stack>
    );
}
