import React, {useEffect, useState} from "react";
import {useNavigate, useParams} from "react-router-dom";
import {createServer} from "../services/serversService";
import {toast} from "material-react-toastify";
import EditArma3ServerSettingsForm from "../components/servers/EditArma3ServerSettingsForm";
import {getMods} from "../services/modsService";
import {Typography} from "@mui/material";
import EditDayZServerSettingsForm from "../components/servers/EditDayZServerSettingsForm";

const ARMA3_INITIAL_STATE = {
    type: "ARMA3",
    name: "",
    description: "",
    port: 2302,
    queryPort: 2303,
    maxPlayers: 32,
    password: "",
    adminPassword: "",
    clientFilePatching: false,
    serverFilePatching: false,
    persistent: true,
    battlEye: true,
    vonEnabled: true,
    verifySignatures: true,
    activeMods: [],
    activeDLCs: [],
    additionalOptions: ""
}

const DAYZ_INITIAL_STATE = {
    "name": "",
    "description": "",
    "port": 2302,
    "queryPort": 2303,
    "maxPlayers": 32,
    "password": "",
    "adminPassword": "",
    "clientFilePatching": false,
    "persistent": true,
    "verifySignatures": false,
    "vonEnabled": true,
    "forceSameBuild": false,
    "thirdPersonViewEnabled": true,
    "crosshairEnabled": true,
    "instanceId": 1,
    "respawnTime": 0,
    "timeAcceleration": 1.0,
    "nightTimeAcceleration": 1.0,
    "additionalOptions": "",
}

const SERVER_NAMES = {
    "ARMA3": "Arma 3",
    "DAYZ": "DayZ",
    "DAYZ_EXP": "DayZ Experimental"
}

const ServerSettingsPage = () => {
    const [availableMods, setAvailableMods] = useState([]);
    const [availableDlcs, setAvailableDlcs] = useState([]);
    const [loading, setLoading] = useState(true);

    const {type} = useParams();

    const navigate = useNavigate();

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        try {
            setLoading(true);
            const {data: modsDto} = await getMods();
            setAvailableMods(modsDto.workshopMods);
            setAvailableDlcs(modsDto.creatorDlcs);
            setLoading(false);
        } catch (e) {
            console.error(e);
            toast("Error while loading mods");
        }
    }

    const handleSubmit = async (values, selectedMods, selectedDlcs) => {
        const server = {
            ...values,
            type,
            activeMods: selectedMods ?? [],
            activeDLCs: selectedDlcs ?? [],
        }

        try {
            await createServer(server);
            toast.success("Server successfully created");
            navigate("/servers");
        } catch (e) {
            console.error(e);
            toast.error("Creating server failed");
        }
    }

    return (
            <>
                <Typography variant="h4" mb={2}>New {SERVER_NAMES[type]} server</Typography>
                {loading && <h2>Loading server data...</h2>}
                {!loading &&
                        <>
                            {type === "ARMA3" &&
                                    <EditArma3ServerSettingsForm server={ARMA3_INITIAL_STATE} onSubmit={handleSubmit}
                                                                 availableMods={availableMods}
                                                                 availableDlcs={availableDlcs}
                                    />
                            }
                            {(type === "DAYZ" || type === "DAYZ_EXP") &&
                                    <EditDayZServerSettingsForm server={DAYZ_INITIAL_STATE} onSubmit={handleSubmit}/>
                            }
                        </>}

            </>
    );
}

export default ServerSettingsPage;