import ModsErrorAlertMessage from "./ModsErrorAlertMessage";
import ModsTable from "./ModsTable";
import CreatePresetDialog from "./CreatePresetDialog";
import {useEffect, useState} from "react";
import {getMods, installMod, uninstallMods, updateMods} from "../../services/modsService";
import {useInterval} from "../../hooks/use-interval";
import {toast} from "material-react-toastify";
import {createModPreset} from "../../services/modPresetsService";

export default function ModsManagement() {
    const [initialLoading, setInitialLoading] = useState(true);
    const [mods, setMods] = useState([]);
    const [selected, setSelected] = useState([]);
    const [filter, setFilter] = useState("");
    const [newPresetDialogOpen, setNewPresetDialogOpen] = useState(false);

    const fetchMods = async () => {
        const {data: modsDto} = await getMods();
        const mods = modsDto.workshopMods.map(mod => {
            const lastUpdated = mod.lastUpdated ? new Date(mod.lastUpdated) : "";
            return {...mod, lastUpdated}
        });
        mods.sort((a, b) => a.name.localeCompare(b.name));

        setMods(mods);
        setInitialLoading(false);
    };

    useEffect(() => {
        fetchMods();
    }, []);

    useInterval(fetchMods, 5000);

    const handleInstall = async (modId) => {
        const {data: mod} = await installMod(modId);
        setMods(prevState => {
            return [mod, ...prevState].sort((a, b) => a.name.localeCompare(b.name));
        })
    };

    const handleUpdate = async () => {
        setMods(prevState => {
            const newMods = [...prevState];
            for (const selectedModId of selected) {
                const selectedMod = newMods.find(mod => mod.id === selectedModId);
                selectedMod.installationStatus = "INSTALLATION_IN_PROGRESS";
                selectedMod.errorStatus = null;
            }
            return newMods;
        })
        await updateMods(selected.join(","));
    };

    const handleUninstall = async () => {
        setMods(prevState => {
            return prevState.filter(mod => selected.indexOf(mod.id) === -1);
        })
        setSelected([]);
        await uninstallMods(selected.join(","));
        toast.success("Mod(s) successfully uninstalled");
    };

    const handleSelectAllClick = (event) => {
        if (event.target.checked) {
            const newSelected = filteredMods.map((n) => n.id);
            setSelected(newSelected);
            return;
        }
        setSelected([]);
    };

    const handleClick = (_, id) => {
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

    const handleFilterChange = (_, newValue) => {
        setSelected([]);
        setFilter(newValue);
    }

    const filterMods = () => {
        if (!filter) {
            return mods;
        }
        return mods.filter(mod => mod.serverType === filter);
    }

    const getSelectedMods = () => {
        return selected.map(id => mods.find(mod => mod.id === id));
    }

    const getSelectedModsSorted = () => {
        return getSelectedMods().sort((a, b) => a.name.localeCompare(b.name));
    }

    const handlePresedDialogOpen = () => {
        setNewPresetDialogOpen(true);
    }

    const handlePresedDialogClose = () => {
        setNewPresetDialogOpen(false);
    }

    const handleCreateNewPreset = async (presetName) => {
        setNewPresetDialogOpen(false);
        const type = mods.find(mod => mod.id === selected[0]).serverType;
        const request = {
            name: presetName,
            mods: selected,
            type
        };
        await createModPreset(request);
        toast.success(`Preset '${presetName}' successfully created`);
    }

    const errorOccured = mods.some(mod => mod.installationStatus === "ERROR");
    const filteredMods = filterMods();
    const arma3ModsCount = mods.filter(mod => mod.serverType === "ARMA3").length;
    const dayZModsCount = mods.filter(mod => mod.serverType === "DAYZ").length;
    const mixedModsSelected = getSelectedMods().map(mod => mod.serverType).filter(
            (v, i, a) => a.indexOf(v) === i).length > 1;

    return <>
        {errorOccured && <ModsErrorAlertMessage mods={mods}/>}
        <ModsTable rows={filteredMods} selected={selected} filter={filter} loading={initialLoading}
                   arma3ModsCount={arma3ModsCount}
                   dayZModsCount={dayZModsCount} mixedModsSelected={mixedModsSelected}
                   onClick={handleClick} onSelectAllClick={handleSelectAllClick}
                   onUpdateClicked={handleUpdate}
                   onUninstallClicked={handleUninstall} onInstallClicked={handleInstall}
                   onFilterChange={handleFilterChange} onCreatePresetClicked={handlePresedDialogOpen}
        />
        <CreatePresetDialog open={newPresetDialogOpen} onClose={handlePresedDialogClose}
                            selectedMods={getSelectedModsSorted()} onConfirmClicked={handleCreateNewPreset}
        />
    </>;
}