import {useEffect, useState} from "react";
import {Backdrop, Box, Button, CircularProgress, Modal, SelectChangeEvent} from "@mui/material";
import ListBuilder from "../../UI/ListBuilder/ListBuilder";
import {serversApi} from "../../api/client";
import {LocalModDto, PresetResponseDto, ServerDto, ServerInstanceInfoDto, ServerLocalModDto, ServerType, ServerWorkshopModDto} from "../../api/generated";
import {Arma3ServerDto, DayZServerDto} from "../../api/serverModels";
import {toast} from "react-toastify";
import MemoryIcon from "@mui/icons-material/Memory";
import {useServer} from "../../hooks/queries/useServer";
import {useMods} from "../../hooks/queries/useMods";
import {useLocalMods} from "../../hooks/queries/useLocalMods";
import {usePresets} from "../../hooks/queries/usePresets";
import {useQueryClient} from "@tanstack/react-query";
import {queryKeys} from "../../api/queryKeys";
import {usePermission} from "../../hooks/usePermission";

type ModItem = {
    id: string;
    name?: string;
    subtitle?: string;
    source: 'WORKSHOP' | 'LOCAL';
    originalId: number;
    position?: number;
};

function toModItem(mod: ServerWorkshopModDto | ServerLocalModDto, source: 'WORKSHOP' | 'LOCAL'): ModItem {
    return {
        id: `${source.toLowerCase()}-${mod.id}`,
        name: mod.name,
        subtitle: source === 'LOCAL' ? '(LOCAL)' : undefined,
        source,
        originalId: mod.id!,
        position: mod.position,
    };
}

function localModToModItem(mod: LocalModDto): ModItem {
    return {
        id: `local-${mod.id}`,
        name: mod.name,
        subtitle: '(LOCAL)',
        source: 'LOCAL',
        originalId: mod.id!,
    };
}

type ListBuilderModEditProps = {
    server: ServerDto
    status: ServerInstanceInfoDto | null
}

const ListBuilderModEdit = (props: ListBuilderModEditProps) => {
    const queryClient = useQueryClient();
    const canModify = usePermission('MOD_MODIFY');
    const [isOpen, setIsOpen] = useState(false);
    const [availableMods, setAvailableMods] = useState<Array<ModItem>>([]);
    const [selectedMods, setSelectedMods] = useState<Array<ModItem>>([]);
    const [selectedPreset, setSelectedPreset] = useState("");

    const serverType = props.server.type as ServerType;

    const {data: serverData, isLoading: serverLoading} = useServer(props.server.id, {enabled: isOpen});
    const {data: workshopModsData = [], isLoading: modsLoading} = useMods(serverType, {enabled: isOpen});
    const {data: localModsData = [], isLoading: localModsLoading} = useLocalMods(serverType, {enabled: isOpen});
    const {data: presetsData = [], isLoading: presetsLoading} = usePresets(serverType, {enabled: isOpen});

    const isLoading = isOpen && (serverLoading || modsLoading || localModsLoading || presetsLoading);

    useEffect(() => {
        if (!isOpen || !serverData || modsLoading || localModsLoading || presetsLoading) return;

        const serverTyped = serverData as Arma3ServerDto | DayZServerDto;
        const activeWorkshopMods: ServerWorkshopModDto[] = serverTyped.activeMods ?? [];
        const activeLocalModsOnServer: ServerLocalModDto[] = serverTyped.activeLocalMods ?? [];

        const selectedWorkshopItems = activeWorkshopMods.map(m => toModItem(m, 'WORKSHOP'));
        const selectedLocalItems = activeLocalModsOnServer.map((m: ServerLocalModDto) => toModItem(m, 'LOCAL'));
        const allSelected: ModItem[] = [...selectedWorkshopItems, ...selectedLocalItems]
            .sort((a, b) => (a.position !== undefined ? a.position : Infinity) - (b.position !== undefined ? b.position : Infinity));

        const selectedWorkshopIds = new Set(activeWorkshopMods.map(m => m.id));
        const selectedLocalIds = new Set(activeLocalModsOnServer.map(m => m.id));

        const availableWorkshopItems = (workshopModsData as unknown as ServerWorkshopModDto[])
            .filter(m => !selectedWorkshopIds.has(m.id))
            .map(m => toModItem(m, 'WORKSHOP'));

        const availableLocalItems = (localModsData as LocalModDto[])
            .filter(m => !selectedLocalIds.has(m.id!))
            .map(m => localModToModItem(m));

        const allAvailable: ModItem[] = [...availableWorkshopItems, ...availableLocalItems]
            .sort((a, b) => (a.name ?? "").localeCompare(b.name ?? ""));

        setSelectedMods(allSelected);
        setAvailableMods(allAvailable);
        setSelectedPreset("");
    }, [isOpen, serverData, workshopModsData, localModsData, modsLoading, localModsLoading, presetsLoading]);

    const serverRunning = props.status != null && props.status.alive;

    function handleManageModsButtonClick() {
        if (props.server.id === undefined) return;
        setIsOpen(true);
    }

    function handleModSelect(option: ModItem) {
        setSelectedPreset("");
        setAvailableMods(prev => prev.filter(item => item !== option));
        setSelectedMods(prev =>
            [option, ...prev].sort((a, b) => (a.name ?? "").localeCompare(b.name ?? ""))
        );
    }

    function handleModDeselect(option: ModItem) {
        setSelectedPreset("");
        setSelectedMods(prev => prev.filter(item => item !== option));
        setAvailableMods(prev =>
            [option, ...prev].sort((a, b) => (a.name ?? "").localeCompare(b.name ?? ""))
        );
    }

    function handleModReorder(items: ModItem[]) {
        setSelectedMods(items);
    }

    function handleSelectAll() {
        setSelectedPreset("");
        setSelectedMods(prev => [...prev, ...availableMods].sort((a, b) => (a.name ?? "").localeCompare(b.name ?? "")));
        setAvailableMods([]);
    }

    function handleClearAll() {
        setSelectedPreset("");
        setAvailableMods(prev => [...prev, ...selectedMods].sort((a, b) => (a.name ?? "").localeCompare(b.name ?? "")));
        setSelectedMods([]);
    }

    function handlePresetChange(e: SelectChangeEvent) {
        const presetId = Number(e.target.value);
        const preset = presetsData.find((preset: PresetResponseDto) => preset.id === presetId);
        if (!preset) return;

        // TODO see if local mods may be a part of preset
        // Presets only apply to workshop mods; preserve local mod selection state
        const localSelected = selectedMods.filter(m => m.source === 'LOCAL');
        const workshopAvailable = [...availableMods.filter(m => m.source === 'WORKSHOP'), ...selectedMods.filter(m => m.source === 'WORKSHOP')];

        const newSelectedWorkshop: ModItem[] = [];
        for (const mod of preset.mods ?? []) {
            const found = workshopAvailable.find(m => m.originalId === mod.id);
            if (!found) continue;
            workshopAvailable.splice(workshopAvailable.indexOf(found), 1);
            newSelectedWorkshop.push(found);
        }

        const newAvailable = [...workshopAvailable, ...availableMods.filter(m => m.source === 'LOCAL')]
            .sort((a, b) => (a.name ?? "").localeCompare(b.name ?? ""));

        setAvailableMods(newAvailable);
        setSelectedMods([...newSelectedWorkshop, ...localSelected]);
        setSelectedPreset(String(presetId));
    }

    async function handleConfirm() {
        if (props.server.id === undefined) return;

        const activeMods: ServerWorkshopModDto[] = selectedMods
            .filter(m => m.source === 'WORKSHOP')
            .map(m => ({id: m.originalId, name: m.name ?? '', position: selectedMods.indexOf(m)}));

        const activeLocalMods: ServerLocalModDto[] = selectedMods
            .filter(m => m.source === 'LOCAL')
            .map(m => ({id: m.originalId, name: m.name ?? '', position: selectedMods.indexOf(m)}));

        setIsOpen(false);
        try {
            await serversApi.updateServer({
                id: props.server.id,
                serverDto: {...serverData, activeMods, activeLocalMods} as unknown as ServerDto
            });
            toast.success("Mods successfully set");
            await queryClient.invalidateQueries({queryKey: queryKeys.servers});
        } catch (e: any) {
            console.error(e);
            toast.error(e.data?.response || "Failed to update the server");
        }
    }

    function handleClose() {
        setIsOpen(false);
    }

    return (
        <>
            <Backdrop open={isLoading}>
                <CircularProgress color="inherit"/>
            </Backdrop>
            <Button id="manage-mods-btn" onClick={handleManageModsButtonClick} startIcon={<MemoryIcon/>} variant="contained">
                Mods
            </Button>
            <Modal open={isOpen && !isLoading} onClose={handleClose}>
                <Box>
                    <ListBuilder selectedOptions={selectedMods} availableOptions={availableMods}
                                 onSelect={canModify ? handleModSelect : () => {}}
                                 onDeselect={canModify ? handleModDeselect : () => {}}
                                 onReorder={canModify ? handleModReorder : undefined}
                                 onSelectAll={canModify ? handleSelectAll : undefined}
                                 onClearAll={canModify ? handleClearAll : undefined}
                                 itemsLabel="mods" showFilter selectedPreset={selectedPreset} presets={presetsData}
                                 onPresetChange={canModify ? handlePresetChange : () => {}}
                                 withControls
                                 onConfirm={handleConfirm} onCancel={handleClose}
                                 confirmDisabled={serverRunning || !canModify}
                    />
                </Box>
            </Modal>
        </>
    );
};

export default ListBuilderModEdit;
