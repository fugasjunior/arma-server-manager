import {
    Alert,
    AlertTitle,
    Button,
    Card,
    CardActions,
    CardContent,
    CardMedia,
    LinearProgress,
    SelectChangeEvent,
    Stack,
    Typography
} from "@mui/material";
import arma3Logo from "../../img/arma3_logo.jpg";
import dayZLogo from "../../img/dayz_logo.jpg";
import dayZExpLogo from "../../img/dayz_exp_logo.jpg";
import reforgerLogo from "../../img/reforger_logo.jpg";
import SERVER_NAMES from "../../util/serverNames";
import config from "../../config";
import workshopErrorStatusMap from "../../util/workshopErrorStatusMap";
import {ErrorStatus, ServerInstallationDto, ServerType, SteamCmdItemInfoDto, SteamCmdStatus} from "../../api/generated";
import {ServerBranchSelect} from "./ServerBranchSelect.tsx";
import {humanFileSize} from "../../util/util.ts";

const SERVER_IMAGE_URLS = new Map<ServerType, string>([
    [ServerType.Arma3, arma3Logo],
    [ServerType.Dayz, dayZLogo],
    [ServerType.DayzExp, dayZExpLogo],
    [ServerType.Reforger, reforgerLogo]
]);

const isInstalling = (installation: ServerInstallationDto) => {
    return installation.installationStatus === "INSTALLATION_IN_PROGRESS";
}

type ServerInstallationItemProps = {
    installation: ServerInstallationDto,
    onUpdateClicked: (serverType: ServerType) => void,
    onBranchChanged: (e: SelectChangeEvent, serverType: ServerType) => Promise<void>,
    steamCmdItemInfo: SteamCmdItemInfoDto | undefined
}

const ServerInstallationItem = (props: ServerInstallationItemProps) => {
    const {installation, steamCmdItemInfo, onUpdateClicked, onBranchChanged} = props;

    const hasMultipleAvailableBranches = () => (installation.availableBranches?.size ?? 0) > 1;

    const getProgressBar = () => {
        if (steamCmdItemInfo && steamCmdItemInfo.bytesTotal) {
            const progressPercent = ((steamCmdItemInfo.bytesFinished ?? 0) / steamCmdItemInfo.bytesTotal) * 100;
            return <LinearProgress variant="determinate" value={progressPercent}/>
        }
        return <LinearProgress/>;
    };

    const getActionButton = () => {
        if (isInstalling(installation) && steamCmdItemInfo === undefined) {
            return <Button fullWidth variant="contained" disabled>
                {installation.lastUpdatedAt === null ?
                    "Installing..." : "Updating..."
                }
            </Button>
        }

        if (isInstalling(installation) && steamCmdItemInfo !== undefined) {
            let displayText = "";
            if (steamCmdItemInfo.status === SteamCmdStatus.Finished) {
                displayText = "Dry running";
            } else if (steamCmdItemInfo.status === SteamCmdStatus.InQueue) {
                displayText = "In queue";
            } else {
                displayText = steamCmdItemInfo.status ?? "";
            }

            return <Button fullWidth variant="contained" disabled>
                {displayText.toUpperCase()}&nbsp;
                {steamCmdItemInfo.status !== SteamCmdStatus.Finished && steamCmdItemInfo.status !== SteamCmdStatus.InQueue &&
                    `(${humanFileSize(steamCmdItemInfo.bytesFinished ?? 0)} / ${humanFileSize(steamCmdItemInfo.bytesTotal ?? 0)})`}
            </Button>
        }

        return <Button fullWidth variant="contained"
                       data-testid={`install-update-btn-${installation.type}`}
                       onClick={() => onUpdateClicked(installation.type! as ServerType)}
                       color={installation.errorStatus == null ? "primary" : "error"}
        >
            {installation.errorStatus !== null && "Retry "}
            {installation.lastUpdatedAt === null ? "Install" : "Update"}
        </Button>
    }

    return (
        <Card sx={{maxWidth: "540px"}} data-testid={`install-card-${installation.type}`}>
            <CardMedia
                component="img"
                height="140"

                image={SERVER_IMAGE_URLS.get(installation.type!)}
                alt="game banner"
            />
            <CardContent>
                {installation.errorStatus &&
                    <Alert severity="error" sx={{mb: 2}}>
                        <AlertTitle>Error</AlertTitle>
                        {workshopErrorStatusMap.get(installation.errorStatus as ErrorStatus)
                            ?? "Unknown error"}
                    </Alert>
                }

                <Stack direction="row" justifyContent="space-between">
                    <Typography gutterBottom variant="h5">
                        {SERVER_NAMES.get(installation.type!)}
                    </Typography>

                    {hasMultipleAvailableBranches() &&
                        <ServerBranchSelect installation={installation}
                                            onChange={(e) => onBranchChanged(e, installation.type!)}/>
                    }
                </Stack>

                {!isInstalling(installation) ?
                    <Stack>
                        {installation.version &&
                            <Typography variant="body2" color="text.secondary">
                                Version: <strong> {installation.version}</strong>
                            </Typography>
                        }
                        {installation.lastUpdatedAt &&
                            <Typography variant="body2" color="text.secondary">
                                Last updated:&nbsp;
                                <strong>
                                    {new Date(installation.lastUpdatedAt).toLocaleString(undefined,
                                        config.dateFormat)}
                                </strong>
                            </Typography>
                        }
                    </Stack>
                    :
                    getProgressBar()
                }
            </CardContent>
            <CardActions data-testid={`install-status-${installation.type}`}>
                {getActionButton()}
            </CardActions>
        </Card>
    );
};

export default ServerInstallationItem;