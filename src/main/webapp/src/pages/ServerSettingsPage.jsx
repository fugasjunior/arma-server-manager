import React, {useEffect, useState} from "react";
import {useNavigate, useParams} from "react-router-dom";
import {getServer, updateServer} from "../services/serversService";
import {toast} from "material-react-toastify";
import ListBuilder from "../UI/ListBuilder";
import {getMods} from "../services/modsService";
import {Box, Button, Modal, Typography} from "@mui/material";
import EditArma3ServerSettingsForm from "../components/servers/EditArma3ServerSettingsForm";
import EditDayZServerSettingsForm from "../components/servers/EditDayZServerSettingsForm";

const SERVER_NAMES = {
    "ARMA3": "Arma 3",
    "DAYZ": "DayZ",
    "DAYZ_EXP": "DayZ Experimental"
}

const ServerSettingsPage = () => {
    const {id} = useParams();
    const [server, setServer] = useState({});
    const [availableMods, setAvailableMods] = useState([]);
    const [selectedMods, setSelectedMods] = useState([]);
    const [availableDlcs, setAvailableDlcs] = useState([]);
    const [selectedDlcs, setSelectedDlcs] = useState([]);
    const [loading, setLoading] = useState(true);
    const [modsModalOpen, setModsModalOpen] = useState(false);
    const [dlcsModalOpen, setDlcsModalOpen] = useState(false);

    const navigate = useNavigate();

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        try {
            setLoading(true);
            const {data: fetchedServer} = await getServer(id);
            const {data: modsDto} = await getMods();

            const newSelectedMods = fetchedServer.activeMods;
            const newAvailableMods = modsDto.workshopMods.filter(
                    mod => !newSelectedMods.find(searchedMod => searchedMod.id === mod.id));
            const newSelectedDlcs = fetchedServer.activeDLCs;
            const newAvailableDlcs = modsDto.creatorDlcs.filter(
                    cdlc => !newSelectedDlcs.find(searchedDlc => searchedDlc.id === cdlc.id));

            setServer(fetchedServer);
            setSelectedMods(newSelectedMods);
            setAvailableMods(newAvailableMods);
            setSelectedDlcs(newSelectedDlcs);
            setAvailableDlcs(newAvailableDlcs);
            setLoading(false);
        } catch (e) {
            console.error(e);
            toast("Error while loading server data");
        }
    }

    const handleSubmit = async values => {
        const request = {
            ...values,
            type: server.type,
            queryPort: server.type === "ARMA3" ? server.port + 1 : server.queryPort,
            activeMods: selectedMods,
            activeDLCs: selectedDlcs,
            instanceInfo: null,
        }

        try {
            await updateServer(id, request);
            toast.success("Server successfully updated");
        } catch (e) {
            console.error(e);
        }
    }

    const handleCancel = () => {
        navigate("/servers");
    }

    const handleModSelect = option => {
        setAvailableMods((prevState) => {
            return prevState.filter(item => item !== option);
        });

        setSelectedMods((prevState) => {
            return [option, ...prevState].sort((a, b) => a.name.localeCompare(b.name));
        });
    }

    const handleModDeselect = option => {
        setSelectedMods((prevState) => {
            return prevState.filter(item => item !== option);
        });

        setAvailableMods((prevState) => {
            return [option, ...prevState].sort((a, b) => a.name.localeCompare(b.name));
        });
    }

    const handleDlcSelect = option => {
        setAvailableDlcs((prevState) => {
            return prevState.filter(item => item !== option);
        });

        setSelectedDlcs((prevState) => {
            return [option, ...prevState].sort((a, b) => a.name.localeCompare(b.name));
        });
    }

    const handleDlcDeselect = option => {
        setSelectedDlcs((prevState) => {
            return prevState.filter(item => item !== option);
        });

        setAvailableDlcs((prevState) => {
            return [option, ...prevState].sort((a, b) => a.name.localeCompare(b.name));
        });
    }

    const handleToggleModsModal = () => {
        setModsModalOpen(prevState => !prevState);
    }

    const handleToggleDlcsModal = () => {
        setDlcsModalOpen(prevState => !prevState);
    }

    return (
            <>
                <Typography variant="h4" mb={2}>Server Settings ({SERVER_NAMES[server.type]})</Typography>
                {loading && <h2>Loading server data...</h2>}
                {!loading &&
                        <>
                            {server.type === "ARMA3" &&
                                    <EditArma3ServerSettingsForm server={server} onSubmit={handleSubmit}
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
                            <Button onClick={handleToggleModsModal} sx={{mt: 2}}>Manage mods</Button>
                            <Button onClick={handleToggleDlcsModal} sx={{mt: 2, ml: 2}}>Manage DLCs</Button>
                            <Modal open={modsModalOpen} onClose={handleToggleModsModal}>

                                <ListBuilder selectedOptions={selectedMods} availableOptions={availableMods}
                                             onSelect={handleModSelect} onDeselect={handleModDeselect}
                                             itemsLabel="mods" showFilter/>
                            </Modal>
                            <Modal open={dlcsModalOpen} onClose={handleToggleDlcsModal}>
                                <ListBuilder selectedOptions={selectedDlcs} availableOptions={availableDlcs}
                                             onSelect={handleDlcSelect} onDeselect={handleDlcDeselect}
                                             itemsLabel="DLCs"/>
                            </Modal>
                        </>}

            </>
    );
}

export default ServerSettingsPage;