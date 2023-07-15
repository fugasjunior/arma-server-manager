import {
    Alert,
    AlertTitle,
    Button,
    Card,
    CardActions,
    CardContent,
    CardMedia,
    LinearProgress,
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
import {ServerInstallationDto} from "../../dtos/ServerInstallationDto.ts";
import {ErrorStatus} from "../../dtos/Status.ts";
import {ServerType} from "../../dtos/ServerDto.ts";

const SERVER_IMAGE_URLS = new Map<ServerType, string>([
    [ServerType.ARMA3, arma3Logo],
    [ServerType.DAYZ, dayZLogo],
    [ServerType.DAYZ_EXP, dayZExpLogo],
    [ServerType.REFORGER, reforgerLogo]
]);

const isInstalling = (installation: ServerInstallationDto) => {
    return installation.installationStatus === "INSTALLATION_IN_PROGRESS";
}

type ServerInstallationItemProps = {
    installation: ServerInstallationDto,
    onUpdateClicked: (serverType: ServerType) => void
}

const ServerInstallationItem = (props: ServerInstallationItemProps) => {
    const {installation, onUpdateClicked} = props;

    return (
        <Card sx={{maxWidth: "540px"}}>
            <CardMedia
                component="img"
                height="140"

                image={SERVER_IMAGE_URLS.get(installation.type)}
                alt="game banner"
            />
            <CardContent>
                {installation.errorStatus &&
                    <Alert severity="error" sx={{mb: 2}}>
                        <AlertTitle>Error</AlertTitle>
                        {workshopErrorStatusMap.get(ErrorStatus[installation.errorStatus as keyof typeof ErrorStatus])
                            ?? "Unknown error"}
                    </Alert>
                }
                <Typography gutterBottom variant="h5">
                    {SERVER_NAMES.get(installation.type)}
                </Typography>
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
                    <LinearProgress/>
                }
            </CardContent>
            <CardActions>
                {isInstalling(installation) ?
                    <Button fullWidth variant="contained" disabled>
                        {installation.lastUpdatedAt === null ?
                            "Installing..." : "Updating..."
                        }
                    </Button>
                    :
                    <Button fullWidth variant="contained"
                            onClick={() => onUpdateClicked(ServerType[installation.type])}
                            color={installation.errorStatus === null ? "primary" : "error"}
                    >
                        {installation.errorStatus !== null && "Retry "}
                        {installation.lastUpdatedAt === null ? "Install" : "Update"}
                    </Button>
                }
            </CardActions>
        </Card>
    );
};

export default ServerInstallationItem;