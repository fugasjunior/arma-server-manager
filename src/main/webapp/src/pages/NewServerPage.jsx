import React, {useContext, useEffect, useState} from "react";
import {useNavigate, useParams} from "react-router-dom";
import {createServer} from "../services/serversService";
import {toast} from "material-react-toastify";
import EditArma3ServerSettingsForm from "../components/servers/EditArma3ServerSettingsForm";
import {getMods} from "../services/modsService";
import {Typography} from "@mui/material";
import EditDayZServerSettingsForm from "../components/servers/EditDayZServerSettingsForm";
import SERVER_NAMES from "../util/serverNames";
import EditReforgerServerSettingsForm from "../components/servers/EditReforgerServerSettingsForm";
import {OSContext} from "../store/os-context";

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
    name: "",
    description: "",
    port: 2302,
    queryPort: 2303,
    maxPlayers: 32,
    password: "",
    adminPassword: "",
    clientFilePatching: false,
    persistent: true,
    verifySignatures: false,
    vonEnabled: true,
    forceSameBuild: false,
    thirdPersonViewEnabled: true,
    crosshairEnabled: true,
    instanceId: 1,
    respawnTime: 0,
    timeAcceleration: 1.0,
    nightTimeAcceleration: 1.0,
    activeMods: [],
    additionalOptions: "",
}

const REFORGER_INITIAL_STATE = {
    type: "REFORGER",
    name: "",
    description: "",
    dedicatedServerId: "",
    scenarioId: "{ECC61978EDCC2B5A}Missions/23_Campaign.conf",
    port: 2001,
    queryPort: 17777,
    maxPlayers: 32,
    password: "",
    adminPassword: "",
    battlEye: true,
    thirdPersonViewEnabled: true,
}

const NewServerPage = () => {
    const [availableMods, setAvailableMods] = useState([]);
    const [availableDlcs, setAvailableDlcs] = useState([]);
    const [loading, setLoading] = useState(true);

    const {type} = useParams();
    const navigate = useNavigate();
    const osCtx = useContext(OSContext);

    useEffect(() => {
        if (type === "DAYZ" && osCtx.os === "LINUX") {
            navigate("/servers");
            toast.error("DayZ server is available only on Windows machines");
            return;
        }
        fetchData();
    }, []);

    const fetchData = async () => {
        try {
            setLoading(true);
            const {data: modsDto} = await getMods(type);
            setAvailableMods(modsDto.workshopMods.sort((a, b) => a.name.localeCompare(b.name)));
            setAvailableDlcs(modsDto.creatorDlcs.sort((a, b) => a.name.localeCompare(b.name)));
            setLoading(false);
        } catch (e) {
            console.error(e);
            toast("Error while loading mods");
        }
    };

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
    };

    const handleCancel = () => {
        navigate("/servers");
    };

    return (
            <>
                <Typography variant="h4" mb={2}>New {SERVER_NAMES[type]} server</Typography>
                {loading && <h2>Loading server data...</h2>}
                {!loading &&
                        <>
                            {type === "ARMA3" &&
                                    <EditArma3ServerSettingsForm server={ARMA3_INITIAL_STATE}
                                                                 availableMods={availableMods}
                                                                 availableDlcs={availableDlcs}
                                                                 onCancel={handleCancel}
                                                                 onSubmit={handleSubmit}
                                    />
                            }
                            {(type === "DAYZ" || type === "DAYZ_EXP") &&
                                    <EditDayZServerSettingsForm server={{...DAYZ_INITIAL_STATE, type}}
                                                                availableMods={availableMods}
                                                                onCancel={handleCancel}
                                                                onSubmit={handleSubmit}
                                    />
                            }
                            {(type === "REFORGER") &&
                                    <EditReforgerServerSettingsForm server={REFORGER_INITIAL_STATE}
                                                                    onCancel={handleCancel}
                                                                    onSubmit={handleSubmit}
                                    />
                            }
                        </>}

            </>
    );
}

export default NewServerPage;