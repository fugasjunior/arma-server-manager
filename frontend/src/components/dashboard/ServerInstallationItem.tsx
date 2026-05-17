import {
    Alert,
    AlertTitle,
    Button,
    Card,
    CardActions,
    CardContent,
    CardMedia,
    Dialog,
    DialogActions,
    DialogContent,
    DialogContentText,
    DialogTitle,
    IconButton,
    LinearProgress,
    ListItemIcon,
    ListItemText,
    Menu,
    MenuItem,
    SelectChangeEvent,
    Stack,
    Typography
} from "@mui/material";
import MoreVertIcon from "@mui/icons-material/MoreVert";
import DeleteForeverIcon from "@mui/icons-material/DeleteForever";
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
import React, {useState} from "react";

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
    onUninstallConfirmed: (serverType: ServerType) => Promise<void>,
    steamCmdItemInfo: SteamCmdItemInfoDto | undefined
}

const ServerInstallationItem = (props: ServerInstallationItemProps) => {
    const {installation, steamCmdItemInfo, onUpdateClicked, onBranchChanged, onUninstallConfirmed} = props;

    const [menuAnchor, setMenuAnchor] = useState<null | HTMLElement>(null);
    const [confirmOpen, setConfirmOpen] = useState(false);

    const isInstalled = installation.lastUpdatedAt !== null && installation.lastUpdatedAt !== undefined;

    const handleMenuOpen = (e: React.MouseEvent<HTMLElement>) => setMenuAnchor(e.currentTarget);
    const handleMenuClose = () => setMenuAnchor(null);
    const handleUninstallClick = () => {
        handleMenuClose();
        setConfirmOpen(true);
    };
    const handleConfirmClose = () => setConfirmOpen(false);
    const handleConfirmUninstall = async () => {
        setConfirmOpen(false);
        await onUninstallConfirmed(installation.type! as ServerType);
    };

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
        <>
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

                    <Stack direction="row" alignItems="center" spacing={1}>
                        {hasMultipleAvailableBranches() &&
                            <ServerBranchSelect installation={installation}
                                                onChange={(e) => onBranchChanged(e, installation.type!)}/>
                        }
                        {isInstalled && (
                            <IconButton
                                size="small"
                                onClick={handleMenuOpen}
                                data-testid={`install-menu-btn-${installation.type}`}
                                aria-label="More options"
                            >
                                <MoreVertIcon/>
                            </IconButton>
                        )}
                    </Stack>
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

        <Menu anchorEl={menuAnchor} open={Boolean(menuAnchor)} onClose={handleMenuClose}>
            <MenuItem
                onClick={handleUninstallClick}
                disabled={isInstalling(installation)}
                data-testid={`uninstall-btn-${installation.type}`}
            >
                <ListItemIcon><DeleteForeverIcon color="error"/></ListItemIcon>
                <ListItemText primaryTypographyProps={{color: "error"}}>Uninstall</ListItemText>
            </MenuItem>
        </Menu>

        <Dialog open={confirmOpen} onClose={handleConfirmClose}>
            <DialogTitle>Uninstall {SERVER_NAMES.get(installation.type!)}?</DialogTitle>
            <DialogContent>
                <DialogContentText>
                    This will permanently delete all server files including scenarios, custom profiles, and
                    save files. This cannot be undone.
                </DialogContentText>
            </DialogContent>
            <DialogActions>
                <Button onClick={handleConfirmClose}>Cancel</Button>
                <Button
                    onClick={handleConfirmUninstall}
                    color="error"
                    variant="contained"
                    data-testid={`uninstall-confirm-${installation.type}`}
                >
                    Uninstall
                </Button>
            </DialogActions>
        </Dialog>
        </>
    );
};

export default ServerInstallationItem;