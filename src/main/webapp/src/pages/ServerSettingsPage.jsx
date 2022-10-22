import React, {useEffect, useState} from "react";
import "./ServersPage.css";
import {useParams} from "react-router-dom";
import {getServer, updateServer} from "../services/serversService";
import {toast} from "react-toastify";
import ListBuilder from "../UI/ListBuilder";
import EditServerSettingsForm from "../components/editServerSettingsForm";
import {getMods} from "../services/modsService";

const ServerSettingsPage = () => {
    const {id} = useParams();
    const [server, setServer] = useState({});
    const [availableMods, setAvailableMods] = useState([]);
    const [selectedMods, setSelectedMods] = useState([]);
    const [loading, setLoading] = useState(true);

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

            setServer(fetchedServer);
            setSelectedMods(newSelectedMods);
            setAvailableMods(newAvailableMods);
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
            activeMods: selectedMods,
            instanceInfo: null,
        }

        try {
            await updateServer(id, request);
            toast.success("Server settings successfully changed");
        } catch (e) {
            console.error(e);
            toast.error("Submitting server settings failed");
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

    return (
            <>
                <h1>Server</h1>
                {loading && <h2>Loading server data...</h2>}
                {!loading &&
                        <>
                            <p>Type: {server.type}</p>
                            <EditServerSettingsForm server={server} onSubmit={handleSubmit}/>
                            <ListBuilder selectedOptions={selectedMods} availableOptions={availableMods}
                                         onSelect={handleModSelect} onDeselect={handleModDeselect}/>
                        </>}

            </>
    );
}

export default ServerSettingsPage;