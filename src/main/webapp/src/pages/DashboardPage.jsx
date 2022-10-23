import {deleteServer, getServers, restartServer, startServer, stopServer} from "../services/serversService"
import {useEffect, useState} from "react";
import {Link} from "react-router-dom";
import {useInterval} from "../hooks/use-interval";
import ServerListEntry from "../components/servers/ServerListEntry";
import {Button, Divider, Fab, Stack} from "@mui/material";
import AddCircleOutlineIcon from '@mui/icons-material/AddCircleOutline';
import SystemResourcesMonitor from "../components/dashboard/SystemResourcesMonitor";

const DashboardPage = () => {

    return (
            <SystemResourcesMonitor />
    )
}

export default DashboardPage;