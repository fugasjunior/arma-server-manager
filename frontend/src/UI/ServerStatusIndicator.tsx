import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import {Stack} from "@mui/material";
import {ServerStatus} from "../api/generated";

type Props = {
    status?: ServerStatus;
};

const statusConfig: Record<ServerStatus, { color: string; label: string }> = {
    [ServerStatus.Off]: {color: "text.disabled", label: "Off"},
    [ServerStatus.Starting]: {color: "warning.main", label: "Starting"},
    [ServerStatus.Running]: {color: "success.main", label: "Running"},
    [ServerStatus.Error]: {color: "error.main", label: "Error"},
};

const ServerStatusIndicator = ({status}: Props) => {
    const config = status ? statusConfig[status] : statusConfig[ServerStatus.Off];

    return (
        <Stack direction="row" spacing={0.75} sx={{alignItems: "center"}}>
            <Box
                sx={{
                    width: 10,
                    height: 10,
                    borderRadius: "50%",
                    bgcolor: config.color,
                    flexShrink: 0,
                }}
            />
            <Typography variant="body2" sx={{color: config.color, whiteSpace: "nowrap"}}>
                {config.label}
            </Typography>
        </Stack>
    );
};

export default ServerStatusIndicator;
