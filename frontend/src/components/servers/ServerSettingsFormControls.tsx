import {Button, Grid, useMediaQuery} from "@mui/material";

type ServerSettingsFormControlsProps = {
    serverRunning: boolean | undefined,
    onCancel: () => void
}
export const ServerSettingsFormControls = ({serverRunning, onCancel}: ServerSettingsFormControlsProps) => {
    const mediaQuery = useMediaQuery('(min-width:600px)');

    return <Grid item xs={12} md={6}>
        <Button id="submit-btn"
                title={serverRunning ? "Stop the server to be able to update the settings." : ""}
                fullWidth={!mediaQuery}
                color="primary" variant="contained" type="submit" size="large">
            Submit
        </Button>
        <Button id="cancel-btn" color="error" variant="outlined"
                fullWidth={!mediaQuery} sx={mediaQuery ? {ml: 1} : {mt: 1}}
                onClick={onCancel}>
            Cancel
        </Button>
    </Grid>
};
