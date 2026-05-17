import ModsErrorAlertMessage from "./ModsErrorAlertMessage";
import ModsTable from "./ModsTable";
import CreatePresetDialog from "./CreatePresetDialog";
import {ChangeEvent, useEffect, useState} from "react";
import {useInterval} from "../../hooks/use-interval";
import {toast} from "react-toastify";
import {ModDto, ServerType, SteamCmdItemInfoDto} from "../../api/generated";
import {modsApi, modPresetsApi, steamCmdApi} from "../../api/client";

type WorkshopItemInfoResponse = {
    [id: string]: SteamCmdItemInfoDto
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
        const {data: modsDto} = await modsApi.getMods();
        const mods: Array<ModDto> = (modsDto.workshopMods ?? []).map((mod: ModDto) => {
            const lastUpdated = mod.lastUpdated ? new Date(mod.lastUpdated) : "";
            return {...mod, lastUpdated} as ModDto;
        });
        mods.sort((a: ModDto, b: ModDto) => (a.name ?? "").localeCompare(b.name ?? ""));

        setMods(mods);
        setInitialLoading(false);
    };

    const fetchSteamCmdItemInfo = async () => {
        const {data} = await steamCmdApi.getSteamCmdItemInfos();
        setSteamCmdItemInfo(data);
    };

    const handleInstall = async (modId: number) => {
        const {data: mod} = await modsApi.updateMod({id: modId});
        setMods(prevState => {
            return [mod, ...prevState].sort((a, b) => (a.name ?? "").localeCompare(b.name ?? ""));
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
                selectedMod.errorStatus = undefined;
            }
            return newMods;
        })
        await modsApi.addMods({modIds: selectedModsIds});
    };

    const handleUninstall = async () => {
        setMods(prevState => {
            return prevState.filter(mod => selectedModsIds.indexOf(mod.id!) === -1);
        })
        setSelectedModsIds([]);
        await modsApi.deleteMods({modIds: selectedModsIds});
        toast.success("Mod(s) successfully uninstalled");
    };

    const handleSelectAllRowsClick = (event: ChangeEvent<HTMLInputElement>) => {
        if (event.target.checked) {
            const newSelected = filteredMods.map((n) => n.id!);
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
        return mods.filter(mod => selectedModsIds.indexOf(mod.id!) !== -1);
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
            type: mod.serverType as ServerType
        };
        await modPresetsApi.createPreset({createPresetRequestDto: request});
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

        await modsApi.setModServerOnly({id: modId, serverOnlyDto: {serverOnly: isServerOnly}});
    };

    const errorOccured = mods.some(mod => mod.installationStatus === "ERROR");
    const filteredMods = filterMods();
    const arma3ModsCount = mods.filter(mod => mod.serverType === ServerType.Arma3).length;
    const dayZModsCount = mods.filter(mod => mod.serverType === ServerType.Dayz).length;
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
