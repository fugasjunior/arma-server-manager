import {useEffect, useState} from "react";
import {headlessClientApi} from "../../../api/client";
import {ServerInstanceInfoDto} from "../../../api/generated";
import {toast} from "react-toastify";
import {Avatar, Stack, Typography} from "@mui/material";
import IconButton from "@mui/material/IconButton";
import RemoveIcon from "@mui/icons-material/Remove";
import {blue, grey} from "@mui/material/colors";
import AddIcon from "@mui/icons-material/Add";

type HeadlessClientControlsProps = {
    serverStatus: ServerInstanceInfoDto | null,
    serverId: number,
    targetCount: number,
    onTargetChanged?: () => void,
}

export function HeadlessClientControls(props: HeadlessClientControlsProps) {
    const [targetCount, setTargetCount] = useState(props.targetCount);
    const [pending, setPending] = useState(false);
    const runningCount = props.serverStatus?.headlessClientsCount ?? 0;

    useEffect(() => {
        setTargetCount(props.targetCount);
    }, [props.targetCount]);

    const setTarget = async (newTarget: number) => {
        const previousCount = targetCount;
        setTargetCount(newTarget);
        setPending(true);
        try {
            await headlessClientApi.setHeadlessClientsTarget({
                id: props.serverId,
                setHeadlessClientsTargetRequest: {targetHeadlessClientsCount: newTarget}
            });
            props.onTargetChanged?.();
        } catch {
            setTargetCount(previousCount);
            toast.error("Failed to update headless client target");
        } finally {
            setPending(false);
        }
    };

    const handleAdd = () => setTarget(targetCount + 1);
    const handleRemove = () => setTarget(targetCount - 1);

    const isRunning = props.serverStatus?.alive ?? false;
    const displayCount = isRunning ? `${runningCount} / ${targetCount}` : `${targetCount}`;

    return <Stack spacing={1}>
        <Typography>Headless clients</Typography>
        <Stack direction="row">
            <IconButton color="primary" aria-label="remove headless client"
                        disabled={pending || targetCount < 1}
                        onClick={handleRemove}
            >
                <RemoveIcon/>
            </IconButton>
            <Avatar sx={targetCount > 0 ? {bgcolor: blue[500]} : {bgcolor: grey[400]}}>
                <Typography variant="caption" sx={{fontSize: isRunning ? '0.7rem' : '0.85rem', lineHeight: 1}}>
                    {displayCount}
                </Typography>
            </Avatar>
            <IconButton color="primary" aria-label="add headless client"
                        disabled={pending}
                        onClick={handleAdd}
            >
                <AddIcon/>
            </IconButton>
        </Stack>
    </Stack>;
}
