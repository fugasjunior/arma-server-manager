import React, {useEffect, useState} from "react";
import {getMods, installMod, uninstallMods, updateMods} from "../services/modsService";
import {toast} from "material-react-toastify";
import ModsTable from "../components/mods/ModsTable";
import {useInterval} from "../hooks/use-interval";
import ModsErrorAlertMessage from "../components/mods/ModsErrorAlertMessage";

const ModsPage = () => {
    const [mods, setMods] = useState([]);
    const [selected, setSelected] = useState([]);
    const [filter, setFilter] = useState("");

    const fetchMods = async () => {
        const {data: modsDto} = await getMods();
        setMods(modsDto.workshopMods.sort((a, b) => a.name.localeCompare(b.name)));
    };

    useEffect(() => {
        fetchMods();
    }, []);

    useInterval(fetchMods, 2000);

    const handleInstall = async (modId) => {
        try {
            const {data: mod} = await installMod(modId);
            setMods(prevState => {
                return [mod, ...prevState].sort((a, b) => a.name.localeCompare(b.name));
            })
        } catch (e) {
            console.error(e);
            toast.error("Error during mod install");
        }
    };

    const handleUpdate = async () => {
        try {
            setMods(prevState => {
                const newMods = [...prevState];
                for (const selectedModId of selected) {
                    const selectedMod = newMods.find(mod => mod.id === selectedModId);
                    selectedMod.installationStatus = "INSTALLATION_QUEUED";
                    selectedMod.errorStatus = null;
                }
                return newMods;
            })
            await updateMods(selected.join(","));
        } catch (e) {
            console.error(e);
            toast.error("Error during mod install");
        }
    };

    const handleUninstall = async () => {
        try {
            // TODO add confirmation modal
            setMods(prevState => {
                return prevState.filter(mod => selected.indexOf(mod.id) === -1);
            })
            setSelected([]);
            await uninstallMods(selected.join(","));
            toast.success("Mod(s) successfully uninstalled");
        } catch (e) {
            console.error(e);
            toast.error("Error during mod uninstall");
        }
    };

    const handleSelectAllClick = (event) => {
        if (event.target.checked) {
            const newSelected = mods.map((n) => n.id);
            setSelected(newSelected);
            return;
        }
        setSelected([]);
    };

    const handleClick = (event, id) => {
        const selectedIndex = selected.indexOf(id);
        let newSelected = [];

        if (selectedIndex === -1) {
            newSelected = newSelected.concat(selected, id);
        } else if (selectedIndex === 0) {
            newSelected = newSelected.concat(selected.slice(1));
        } else if (selectedIndex === selected.length - 1) {
            newSelected = newSelected.concat(selected.slice(0, -1));
        } else if (selectedIndex > 0) {
            newSelected = newSelected.concat(
                    selected.slice(0, selectedIndex),
                    selected.slice(selectedIndex + 1),
            );
        }

        setSelected(newSelected);
    };

    const handleFilterChange = (event, newValue) => {
        setSelected([]);
        setFilter(newValue);
    }

    const filterMods = () => {
        if (!filter) {
            return mods;
        }
        return mods.filter(mod => mod.serverType === filter);
    }

    const errorOccured = mods.some(mod => mod.installationStatus === "ERROR");

    return (
            <>
                {errorOccured && <ModsErrorAlertMessage mods={filterMods()}/>}
            <ModsTable rows={filterMods()} selected={selected} onClick={handleClick} onSelectAllClick={handleSelectAllClick}
                onUpdateClicked={handleUpdate} onUninstallClicked={handleUninstall} onInstallClicked={handleInstall}
                       filter={filter} onFilterChange={handleFilterChange}
            />
            </>
    )
}

export default ModsPage;