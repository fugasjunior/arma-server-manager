import React, {useEffect, useState} from "react";
import {useNavigate} from "react-router-dom";
import {createServer} from "../services/serversService";
import {toast} from "material-react-toastify";
import ListBuilder from "../UI/ListBuilder";
import EditServerSettingsForm from "../components/servers/EditServerSettingsForm";
import {getMods} from "../services/modsService";
import {Button, Modal, Typography} from "@mui/material";

const ServerSettingsPage = () => {
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
            const {data: modsDto} = await getMods();
            setAvailableMods(modsDto.workshopMods);
            setAvailableDlcs(modsDto.creatorDlcs);
            setLoading(false);
        } catch (e) {
            console.error(e);
            toast("Error while loading mods");
        }
    }

    const handleSubmit = async values => {
        const server = {
            ...values,
            type: "ARMA3",
            queryPort: values.port + 1,
            activeMods: selectedMods,
            activeDLCs: selectedDlcs,
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
                <Typography variant="h4">New server</Typography>
                {loading && <h2>Loading server data...</h2>}
                {!loading &&
                        <>
                            <EditServerSettingsForm server={{
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
                                persistent: false,
                                battlEye: false,
                                von: false,
                                verifySignatures: false,
                                additionalOptions: ""
                            }} onSubmit={handleSubmit}/>
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