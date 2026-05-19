import ModsErrorAlertMessage from "./ModsErrorAlertMessage";
import ModsTable from "./ModsTable";
import CreatePresetDialog from "./CreatePresetDialog";
import {ChangeEvent, useMemo, useState} from "react";
import {toast} from "react-toastify";
import {ModDto, ServerType} from "../../api/generated";
import {modsApi, modPresetsApi} from "../../api/client";
import {useMods} from "../../hooks/queries/useMods";
import {useSteamCmdItemInfos} from "../../hooks/queries/useSteamCmdItemInfos";
import {useQueryClient} from "@tanstack/react-query";
import {queryKeys} from "../../api/queryKeys";

export default function ModsManagement() {
    const queryClient = useQueryClient();
    const {data: mods = [], isLoading: initialLoading} = useMods(undefined, {refetchInterval: 5000});
    const {data: steamCmdItemInfo = {}} = useSteamCmdItemInfos({refetchInterval: 5000});

    const [selectedModsIds, setSelectedModsIds] = useState<Array<number>>([]);
    const [filter, setFilter] = useState("");
    const [newPresetDialogOpen, setNewPresetDialogOpen] = useState(false);

    const handleInstall = async (modId: number) => {
        await modsApi.updateMod({id: modId});
        await queryClient.invalidateQueries({queryKey: queryKeys.mods()});
    };

    const handleModUpdate = async () => {
        queryClient.setQueryData(queryKeys.mods(), (old: ModDto[] = []) =>
            old.map(mod =>
                selectedModsIds.includes(mod.id!)
                    ? {...mod, installationStatus: "INSTALLATION_IN_PROGRESS", errorStatus: undefined}
                    : mod
            )
        );
        queryClient.setQueryData(queryKeys.steamCmdItemInfos, (old: Record<string, unknown> = {}) => {
            const updated = {...old};
            for (const id of selectedModsIds) delete updated[id];
            return updated;
        });
        await modsApi.addMods({modIds: selectedModsIds});
        await queryClient.invalidateQueries({queryKey: queryKeys.mods()});
    };

    const handleUninstall = async () => {
        const ids = selectedModsIds;
        setSelectedModsIds([]);
        await modsApi.deleteMods({modIds: ids});
        toast.success("Mod(s) successfully uninstalled");
        await queryClient.invalidateQueries({queryKey: queryKeys.mods()});
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
    };

    const filteredMods = useMemo(() => {
        if (!filter) return mods;
        return mods.filter(mod => mod.serverType === filter);
    }, [mods, filter]);

    const getSelectedMods = (): Array<ModDto> => {
        return mods.filter(mod => selectedModsIds.indexOf(mod.id!) !== -1);
    };

    const handlePresedDialogOpen = () => {
        setNewPresetDialogOpen(true);
    };

    const handlePresedDialogClose = () => {
        setNewPresetDialogOpen(false);
    };

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
        await queryClient.invalidateQueries({queryKey: queryKeys.presets()});
    };

    const handleServerOnlyChanged = async (e: ChangeEvent<HTMLInputElement>, modId: number) => {
        const isServerOnly = e.target.checked;
        await modsApi.setModServerOnly({id: modId, serverOnlyDto: {serverOnly: isServerOnly}});
        await queryClient.invalidateQueries({queryKey: queryKeys.mods()});
    };

    const errorOccured = mods.some(mod => mod.installationStatus === "ERROR");
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
