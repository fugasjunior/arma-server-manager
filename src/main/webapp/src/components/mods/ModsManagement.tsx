import ModsErrorAlertMessage from "./ModsErrorAlertMessage";
import ModsTable from "./ModsTable";
import CreatePresetDialog from "./CreatePresetDialog";
import {ChangeEvent, useEffect, useState} from "react";
import {getMods, installMod, uninstallMods, updateMods} from "../../services/modsService";
import {useInterval} from "../../hooks/use-interval";
import {toast} from "material-react-toastify";
import {createModPreset} from "../../services/modPresetsService";
import {ModDto} from "../../dtos/ModDto.ts";

export default function ModsManagement() {
    const [initialLoading, setInitialLoading] = useState(true);
    const [mods, setMods] = useState<Array<ModDto>>([]);
    const [selectedModsIds, setSelectedModsIds] = useState<Array<number>>([]);
    const [filter, setFilter] = useState("");
    const [newPresetDialogOpen, setNewPresetDialogOpen] = useState(false);

    const fetchMods = async () => {
        const {data: modsDto} = await getMods();
        const mods = modsDto.workshopMods.map((mod: ModDto) => {
            const lastUpdated = mod.lastUpdated ? new Date(mod.lastUpdated) : "";
            return {...mod, lastUpdated}
        });
        mods.sort((a: ModDto, b: ModDto) => a.name.localeCompare(b.name));

        setMods(mods);
        setInitialLoading(false);
    };

    useEffect(() => {
        fetchMods();
    }, []);

    useInterval(fetchMods, 5000);

    const handleInstall = async (modId: number) => {
        const {data: mod} = await installMod(modId);
        setMods(prevState => {
            return [mod, ...prevState].sort((a, b) => a.name.localeCompare(b.name));
        })
    };

    const handleUpdate = async () => {
        setMods(prevState => {
            const newMods = [...prevState];
            for (const selectedModId of selectedModsIds) {
                const selectedMod = newMods.find((mod: ModDto) => mod.id === selectedModId);
                selectedMod.installationStatus = "INSTALLATION_IN_PROGRESS";
                selectedMod.errorStatus = null;
            }
            return newMods;
        })
        await updateMods(selectedModsIds.join(","));
    };

    const handleUninstall = async () => {
        setMods(prevState => {
            return prevState.filter(mod => selectedModsIds.indexOf(mod.id) === -1);
        })
        setSelectedModsIds([]);
        await uninstallMods(selectedModsIds.join(","));
        toast.success("Mod(s) successfully uninstalled");
    };

    const handleSelectAllClick = (event: ChangeEvent<HTMLInputElement>) => {
        if (event.target.checked) {
            const newSelected = filteredMods.map((n) => n.id);
            setSelectedModsIds(newSelected);
            return;
        }
        setSelectedModsIds([]);
    };

    const handleClick = (_: any, id: number) => {
        const selectedIndex = selectedModsIds.indexOf(id);
        let newSelected: Array<number> = [];

        if (selectedIndex === -1) {
            newSelected = newSelected.concat(selectedModsIds, id);
        } else if (selectedIndex === 0) {
            newSelected = newSelected.concat(selectedModsIds.slice(1));
        } else if (selectedIndex === selectedModsIds.length - 1) {
            newSelected = newSelected.concat(selectedModsIds.slice(0, -1));
        } else if (selectedIndex > 0) {
            newSelected = newSelected.concat(
                selectedModsIds.slice(0, selectedIndex),
                selectedModsIds.slice(selectedIndex + 1),
            );
        }

        setSelectedModsIds(newSelected);
    };

    const handleFilterChange = (_: any, newValue: string) => {
        setSelectedModsIds([]);
        setFilter(newValue);
    }

    const filterMods = () => {
        if (!filter) {
            return mods;
        }
        return mods.filter(mod => mod.serverType === filter);
    }

    const getSelectedMods = () => {
        return selectedModsIds.map(id => mods.find(mod => mod.id === id));
    }
    const handlePresedDialogOpen = () => {
        setNewPresetDialogOpen(true);
    }

    const handlePresedDialogClose = () => {
        setNewPresetDialogOpen(false);
    }

    const handleCreateNewPreset = async (presetName: string) => {
        setNewPresetDialogOpen(false);
        const type = mods.find(mod => mod.id === selectedModsIds[0]).serverType;
        const request = {
            name: presetName,
            mods: selectedModsIds,
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
        <ModsTable rows={filteredMods} selected={selectedModsIds} filter={filter} loading={initialLoading}
                   arma3ModsCount={arma3ModsCount}
                   dayZModsCount={dayZModsCount} mixedModsSelected={mixedModsSelected}
                   onClick={handleClick} onSelectAllClick={handleSelectAllClick}
                   onUpdateClicked={handleUpdate}
                   onUninstallClicked={handleUninstall} onInstallClicked={handleInstall}
                   onFilterChange={handleFilterChange} onCreatePresetClicked={handlePresedDialogOpen}
        />
        <CreatePresetDialog open={newPresetDialogOpen} onClose={handlePresedDialogClose}
                            onConfirmClicked={handleCreateNewPreset}
        />
    </>;
}