import React, {useContext, useEffect} from "react";
import {useNavigate, useParams} from "react-router-dom";
import {createServer} from "../services/serversService";
import {toast} from "material-react-toastify";
import EditArma3ServerSettingsForm from "../components/servers/EditArma3ServerSettingsForm";
import {Typography} from "@mui/material";
import EditDayZServerSettingsForm from "../components/servers/EditDayZServerSettingsForm";
import SERVER_NAMES from "../util/serverNames";
import EditReforgerServerSettingsForm from "../components/servers/EditReforgerServerSettingsForm";
import {OsContext} from "../store/os-context";

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
    additionalOptions: "",
    difficultySettings: {
        groupIndicators: 0,
        friendlyTags: 0,
        enemyTags: 0,
        detectedMines: 0,
        commands: 1,
        waypoints: 1,
        weaponInfo: 2,
        stanceIndicator: 2,
        thirdPersonView: 0,
        reducedDamage: false,
        tacticalPing: false,
        staminaBar: false,
        weaponCrosshair: false,
        visionAid: false,
        scoreTable: true,
        deathMessages: true,
        vonID: true,
        mapContent: false,
        autoReport: false,
        cameraShake: true,
        aiLevelPreset: 3,
        skillAI: 0.5,
        precisionAI: 0.5
    },
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
    respawnTime: 5,
    timeAcceleration: 1.0,
    nightTimeAcceleration: 1.0,
    activeMods: [],
    additionalOptions: `
        class Missions 
        {
            class DayZ
            {
                template = "dayzOffline.chernarusplus";
            };
        };`,
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
    activeMods: [],
}

const NewServerPage = () => {
    const {type} = useParams();
    const navigate = useNavigate();
    const osCtx = useContext(OsContext);

    useEffect(() => {
        if (type === "DAYZ" && osCtx.os === "LINUX") {
            navigate("/servers");
            toast.error("DayZ server is available only on Windows machines");
        }
    }, []);

    const handleSubmit = async (values) => {
        const server = {
            ...values,
            type,
            activeMods: [],
            activeDLCs: [],
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
            {type === "ARMA3" &&
                <EditArma3ServerSettingsForm server={ARMA3_INITIAL_STATE}
                                             onCancel={handleCancel}
                                             onSubmit={handleSubmit}
                />
            }
            {(type === "DAYZ" || type === "DAYZ_EXP") &&
                <EditDayZServerSettingsForm server={{...DAYZ_INITIAL_STATE, type}}
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
        </>
    );
}

export default NewServerPage;