import {useEffect, useState} from "react";
import {addHeadlessClient, removeHeadlessClient} from "../../../services/serversService.ts";
import {toast} from "material-react-toastify";
import {Avatar, Stack, Typography} from "@mui/material";
import IconButton from "@mui/material/IconButton";
import RemoveIcon from "@mui/icons-material/Remove";
import {blue, grey} from "@mui/material/colors";
import AddIcon from "@mui/icons-material/Add";

export function HeadlessClientControls(props: { serverStatus: ServerInstanceInfoDto, serverId: number }) {
    const [headlessClientsCount, setHeadlessClientsCount] = useState(props.serverStatus.headlessClientsCount);

    useEffect(() => {
        setHeadlessClientsCount(props.serverStatus.headlessClientsCount)
    }, [props.serverStatus.headlessClientsCount]);

    const handleAddHeadlessClient = async () => {
        setHeadlessClientsCount(prevState => ++prevState);
        await addHeadlessClient(props.serverId);
        toast.success("Headless client started");
    }

    const handleRemoveHeadlessClient = async () => {
        setHeadlessClientsCount(prevState => --prevState);
        await removeHeadlessClient(props.serverId);
        toast.success("Headless client stopped");
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
