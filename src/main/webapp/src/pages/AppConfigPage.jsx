import {deleteServer, getServers, restartServer, startServer, stopServer} from "../services/serversService"
import React, {useEffect, useState} from "react";
import {Link} from "react-router-dom";
import {useInterval} from "../hooks/use-interval";
import ServerListEntry from "../components/servers/ServerListEntry";
import {Button, Divider, Fab, Stack, TextField, Typography} from "@mui/material";
import AddCircleOutlineIcon from '@mui/icons-material/AddCircleOutline';
import ReactTooltip from "react-tooltip";
import SteamAuthForm from "../components/appConfig/SteamAuthForm";

const AppConfigPage = () => {

    return (
            <>
                <Typography variant="h4" component="h2" mb={2}>App configuration</Typography>
                <SteamAuthForm />
            </>
    )
}

export default AppConfigPage;