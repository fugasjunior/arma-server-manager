import {
    Alert,
    AlertTitle, Button,
    Card,
    CardActions,
    CardContent,
    CardMedia,
    LinearProgress,
    Stack,
    Typography
} from "@mui/material";

const INSTALLATION_NAMES = {
    "ARMA3": "Arma 3",
    "ARMA4": "Arma 4",
    "DAYZ": "DayZ",
    "DAYZ_EXP": "DayZ Experimental",
    "REGORGER": "Reforger"
}

const SERVER_IMAGE_URLS = {
    "ARMA3": "./img/arma3_logo.jpg",
    "DAYZ": "./img/dayz_logo.jpg",
    "DAYZ_EXP": "./img/dayz_exp_logo.jpg",
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
                    <Typography gutterBottom variant="h5" component="div">
                        {INSTALLATION_NAMES[installation.type]}
                    </Typography>
                    {!isInstalling(installation) ?
                            <Typography variant="body2" color="text.secondary">
                                <Stack>
                                    {installation.version &&
                                            <p>Version:
                                                <strong> {installation.version}</strong>
                                            </p>
                                    }
                                    {installation.lastUpdatedAt &&
                                            <p>Last updated:
                                                <strong> {installation.lastUpdatedAt}</strong>
                                            </p>
                                    }
                                </Stack>
                            </Typography>
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