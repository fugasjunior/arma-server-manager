import {ServerDto} from "../../../dtos/ServerDto.ts";
import {Avatar, Button, Stack, Typography} from "@mui/material";
import ModEditButton from "../ModEditButton.tsx";
import ListBuilderDLCsEdit from "../ListBuilderDLCsEdit.tsx";
import TextSnippetIcon from "@mui/icons-material/TextSnippet";
import AutomaticRestartSettings from "./AutomaticRestartSettings.tsx";
import IconButton from "@mui/material/IconButton";
import AddIcon from '@mui/icons-material/Add';
import RemoveIcon from '@mui/icons-material/Remove';
import {useState} from "react";
import {blue, grey} from '@mui/material/colors';
import {addHeadlessClient, removeHeadlessClient} from "../../../services/serversService.ts";
import {toast} from "material-react-toastify";

function HeadlessClientControls(props: { serverStatus: ServerInstanceInfoDto, serverId: number }) {
    const [headlessClientsCount, setHeadlessClientsCount] = useState(props.serverStatus.headlessClientsCount);

    const handleAddHeadlessClient = async () => {
        setHeadlessClientsCount(prevState => ++prevState);
        await addHeadlessClient(props.serverId);
        toast.success("Headless client started.");
    }

    const handleRemoveHeadlessClient = async () => {
        setHeadlessClientsCount(prevState => --prevState);
        await removeHeadlessClient(props.serverId);
        toast.success("Headless client stopped.");
    }

    return <Stack spacing={1}>
        <Typography>Headless clients</Typography>
        <Stack direction="row">
            <IconButton color="primary" aria-label="remove headless client"
                        disabled={headlessClientsCount < 1}
                        onClick={handleRemoveHeadlessClient}
            >
                <RemoveIcon/>
            </IconButton>
            <Avatar sx={headlessClientsCount > 0 ? {bgcolor: blue[500]} : {bgcolor: grey[400]}}>
                {headlessClientsCount}
            </Avatar>
            <IconButton color="primary" aria-label="add headless client"
                        onClick={handleAddHeadlessClient}
            >
                <AddIcon/>
            </IconButton>
        </Stack>
    </Stack>;
}

export function ServerListEntryDetails(props: {
    server: ServerDto,
    serverStatus: ServerInstanceInfoDto | null,
    onClick: () => void
}) {
    return <Stack direction="row" spacing={2}>
        <ModEditButton server={props.server} serverStatus={props.serverStatus}/>
        {props.server.type === "ARMA3" &&
            <ListBuilderDLCsEdit status={props.serverStatus} server={props.server}/>}
        <Button
            variant="contained"
            startIcon={<TextSnippetIcon/>} onClick={props.onClick}
            color="info">
            Logs
        </Button>

        <AutomaticRestartSettings serverId={props.server.id!} dto={props.server.automaticRestart}/>

        {props.serverStatus?.alive &&
            <HeadlessClientControls serverId={props.server.id!} serverStatus={props.serverStatus}/>}
    </Stack>;
}
