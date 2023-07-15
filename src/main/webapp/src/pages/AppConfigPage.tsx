import {Typography} from "@mui/material";
import SteamAuthForm from "../components/appConfig/SteamAuthForm";

const AppConfigPage = () => {

    return (
        <>
            <Typography variant="h4" component="h2" mb={2}>App configuration</Typography>
            <SteamAuthForm/>
        </>
    )
}

export default AppConfigPage;