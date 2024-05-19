import ModsErrorAlertMessage from "./ModsErrorAlertMessage";
import ModsTable from "./ModsTable";
import CreatePresetDialog from "./CreatePresetDialog";
import {ChangeEvent, useEffect, useState} from "react";
import {getMods, installMod, setModServerOnly, uninstallMods, updateMods} from "../../services/modsService";
import {useInterval} from "../../hooks/use-interval";
import {toast} from "material-react-toastify";
import {createModPreset} from "../../services/modPresetsService";
import {ModDto} from "../../dtos/ModDto.ts";
import {SteamCmdItemInfoDto} from "../../dtos/SteamCmdItemInfoDto.ts";
import {getItemInfo} from "../../services/steamCmdService.ts";

type WorkshopItemInfoResponse = {
    [id: number]: SteamCmdItemInfoDto
}

export default function ModsManagement() {
    const [initialLoading, setInitialLoading] = useState(true);
    const [mods, setMods] = useState<Array<ModDto>>([]);
    const [selectedModsIds, setSelectedModsIds] = useState<Array<number>>([]);
    const [filter, setFilter] = useState("");
    const [newPresetDialogOpen, setNewPresetDialogOpen] = useState(false);
    const [steamCmdItemInfo, setSteamCmdItemInfo] = useState<WorkshopItemInfoResponse>({});

    useEffect(() => {
        void fetchMods();
        void fetchSteamCmdItemInfo();
    }, []);

    useInterval(() => {
        void fetchMods();
        void fetchSteamCmdItemInfo();
    }, 5000);

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

    const fetchSteamCmdItemInfo = async () => {
        const {data} = await getItemInfo();
        setSteamCmdItemInfo(data);
    };

    const handleInstall = async (modId: number) => {
        const {data: mod} = await installMod(modId);
        setMods(prevState => {
            return [mod, ...prevState].sort((a, b) => a.name.localeCompare(b.name));
        })
    };

    const handleModUpdate = async () => {
        setSteamCmdItemInfo(prevState => {
            const newState = {...prevState};
            for (const selectedModId of selectedModsIds) {
                delete newState[selectedModId];
            }
            return newState;
        })

        setMods(prevState => {
            const newMods = [...prevState];
            for (const selectedModId of selectedModsIds) {
                const selectedMod = newMods.find((mod: ModDto) => mod.id === selectedModId);
                if (!selectedMod) {
                    continue;
                }

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

    const handleSelectAllRowsClick = (event: ChangeEvent<HTMLInputElement>) => {
        if (event.target.checked) {
            const newSelected = filteredMods.map((n) => n.id);
            setSelectedModsIds(newSelected);
            return;
        }
        setSelectedModsIds([]);
    };

    const handleRowClick = (id: string | number) => {
        const selectedIndex = selectedModsIds.indexOf(Number(id));
        let newSelected: Array<number> = [];

        if (selectedIndex === -1) {
            newSelected = newSelected.concat(selectedModsIds, Number(id));
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

    const getSelectedMods = (): Array<ModDto> => {
        return mods.filter(mod => selectedModsIds.indexOf(mod.id) !== -1);
    }
    const handlePresedDialogOpen = () => {
        setNewPresetDialogOpen(true);
    }

    const handlePresedDialogClose = () => {
        setNewPresetDialogOpen(false);
    }

    const handleCreateNewPreset = async (presetName: string) => {
        setNewPresetDialogOpen(false);
        const mod = mods.find(mod => mod.id === selectedModsIds[0]);
        if (!mod) {
            return;
        }

        const request = {
            name: presetName,
            mods: selectedModsIds,
            type: mod.serverType
        };
        await createModPreset(request);
        toast.success(`Preset '${presetName}' successfully created`);
    }

    const handleServerOnlyChanged = async (e: ChangeEvent<HTMLInputElement>, modId: number) => {
        const isServerOnly = e.target.checked;
        setMods(prevState => {
            const newMods = [...prevState];
            const foundMod = newMods.find(mod => mod.id === modId);
            if (!foundMod) {
                return prevState;
            }

            foundMod.serverOnly = isServerOnly;
            return newMods;
        });

        await setModServerOnly(modId, isServerOnly);
    };

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
                   steamCmdItemInfo={steamCmdItemInfo}
                   onRowClick={handleRowClick} onSelectAllRowsClick={handleSelectAllRowsClick}
                   onModUpdateClicked={handleModUpdate}
                   onModUninstallClicked={handleUninstall} onModInstallClicked={handleInstall}
                   onFilterChange={handleFilterChange} onCreatePresetClicked={handlePresedDialogOpen}
                   onServerOnlyChanged={handleServerOnlyChanged}
        />
        <CreatePresetDialog open={newPresetDialogOpen} onClose={handlePresedDialogClose}
                            onConfirmClicked={handleCreateNewPreset}
        />
    </>;
}