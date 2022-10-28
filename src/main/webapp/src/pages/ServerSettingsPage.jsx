import React, {useEffect, useState} from "react";
import {useNavigate, useParams} from "react-router-dom";
import {getServer, updateServer} from "../services/serversService";
import {toast} from "material-react-toastify";
import {getMods} from "../services/modsService";
import {Typography} from "@mui/material";
import EditArma3ServerSettingsForm from "../components/servers/EditArma3ServerSettingsForm";
import EditDayZServerSettingsForm from "../components/servers/EditDayZServerSettingsForm";
import SERVER_NAMES from "../util/serverNames";

const ServerSettingsPage = () => {
    const {id} = useParams();
    const [server, setServer] = useState({});
    const [isLoading, setIsLoading] = useState(true);
    const [availableMods, setAvailableMods] = useState([]);
    const [availableDlcs, setAvailableDlcs] = useState([]);

    const navigate = useNavigate();

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        try {
            setIsLoading(true);
            const {data: fetchedServer} = await getServer(id);
            const {data: modsDto} = await getMods();

            setServer(fetchedServer);
            setAvailableMods(modsDto.workshopMods);
            setAvailableDlcs(modsDto.creatorDlcs);
            setIsLoading(false);
        } catch (e) {
            console.error(e);
            toast("Error while isLoading server data");
        }
    }

    const handleSubmit = async (values, selectedMods, selectedDlcs) => {
        const request = {
            ...values,
            type: server.type,
            queryPort: server.type === "ARMA3" ? values.port + 1 : values.queryPort,
            activeMods: selectedMods,
            activeDLCs: selectedDlcs,
            instanceInfo: null,
        }

        try {
            await updateServer(id, request);
            toast.success("Server successfully updated");
            navigate("/servers");
        } catch (e) {
            console.error(e);
        }
    }

    const handleCancel = () => {
        navigate("/servers");
    }
    
    return (
            <>
                <Typography variant="h4" mb={2}>Server Settings ({SERVER_NAMES[server.type]})</Typography>
                {isLoading && <h2>Loading server data...</h2>}
                {!isLoading &&
                        <>
                            {server.type === "ARMA3" &&
                                    <EditArma3ServerSettingsForm server={server} onSubmit={handleSubmit}
                                                                 availableMods={availableMods}
                                                                 availableDlcs={availableDlcs}
                                                                 onCancel={handleCancel}
                                                                 isServerRunning={server.instanceInfo
                                                                         && server.instanceInfo.alive}
                                    />
                            }
                            {(server.type === "DAYZ" || server.type === "DAYZ_EXP") &&
                                    <EditDayZServerSettingsForm server={server} onSubmit={handleSubmit}
                                                                onCancel={handleCancel}
                                                                isServerRunning={server.instanceInfo
                                                                        && server.instanceInfo.alive}
                                    />
                            }
                        </>}

            </>
    );
}

export default ServerSettingsPage;