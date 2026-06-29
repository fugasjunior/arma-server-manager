import {Grid, Typography} from "@mui/material";
import SteamAuthForm from "../components/appConfig/SteamAuthForm";
import WorkshopCronForm from "../components/appConfig/WorkshopCronForm";
import PermissionGuard from "../components/auth/PermissionGuard";

const AppConfigPage = () => {

    return (
        <>
            <Typography variant="h4" component="h2" sx={{mb: 2}}>Settings</Typography>
            <Grid container spacing={6}>
                <Grid size={{xs: 12, md: 6}}>
                    <PermissionGuard permission="STEAM_AUTH_ADMIN">
                        <SteamAuthForm/>
                    </PermissionGuard>
                </Grid>
                <Grid size={{xs: 12, md: 6}}>
                    <PermissionGuard permission="MANAGE_APP_SETTINGS">
                        <WorkshopCronForm/>
                    </PermissionGuard>
                </Grid>
            </Grid>
        </>
    )
}

export default AppConfigPage;