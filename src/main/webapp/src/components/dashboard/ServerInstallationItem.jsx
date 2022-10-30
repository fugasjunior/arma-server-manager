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

const SERVER_IMAGE_URLS = {
    "ARMA3": arma3Logo,
    "DAYZ": dayZLogo,
    "DAYZ_EXP": dayZExpLogo,
    "REFORGER": reforgerLogo
}

const ERROR_STATE_MESSAGES = {
    GENERIC: "Unidentified error. Please contact the system administrator.",
    IO: "File system I/O error. Please contact the system administrator.",
    NO_SUBSCRIPTION: "The given Steam account doesn't have correct subscription and cannot download the server.",
    TIMEOUT: "The request timed out, please retry.",
    WRONG_AUTH: "Incorrect Steam authorization. Please check username, password and Steam Guard token."
}

const isInstalling = (installation) => {
    return installation.installationStatus === "INSTALLATION_IN_PROGRESS";
}

const ServerInstallationItem = (props) => {
    const {installation, onUpdateClicked} = props;

    return (
            <Card sx={{maxWidth: "540px"}}>
                <CardMedia
                        component="img"
                        height="140"

                        image={SERVER_IMAGE_URLS[installation.type]}
                        alt="game banner"
                />
                <CardContent>
                    {installation.errorStatus &&
                            <Alert severity="error" sx={{mb: 2}}>
                                <AlertTitle>Error</AlertTitle>
                                {ERROR_STATE_MESSAGES[installation.errorStatus]
                                        ?? "Unknown error"}
                            </Alert>
                    }
                    <Typography gutterBottom variant="h5">
                        {SERVER_NAMES[installation.type]}
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
                                            Last updated: <strong> {installation.lastUpdatedAt}</strong>
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
                                    onClick={() => onUpdateClicked(installation.type)}
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