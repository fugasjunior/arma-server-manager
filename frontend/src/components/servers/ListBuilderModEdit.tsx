import {useEffect, useState} from "react";
import {Backdrop, Box, Button, CircularProgress, Modal, SelectChangeEvent} from "@mui/material";
import ListBuilder from "../../UI/ListBuilder/ListBuilder";
import {serversApi} from "../../api/client";
import {PresetResponseDto, ServerDto, ServerInstanceInfoDto, ServerType, ServerWorkshopModDto} from "../../api/generated";
import {Arma3ServerDto, DayZServerDto} from "../../api/serverModels";
import {toast} from "react-toastify";
import MemoryIcon from "@mui/icons-material/Memory";
import {useServer} from "../../hooks/queries/useServer";
import {useMods} from "../../hooks/queries/useMods";
import {usePresets} from "../../hooks/queries/usePresets";
import {useQueryClient} from "@tanstack/react-query";
import {queryKeys} from "../../api/queryKeys";

type ListBuilderModEditProps = {
    server: ServerDto
    status: ServerInstanceInfoDto | null
}

const ListBuilderModEdit = (props: ListBuilderModEditProps) => {
    const queryClient = useQueryClient();
    const [isOpen, setIsOpen] = useState(false);
    const [availableMods, setAvailableMods] = useState<Array<ServerWorkshopModDto>>([]);
    const [selectedMods, setSelectedMods] = useState<Array<ServerWorkshopModDto>>([]);
    const [selectedPreset, setSelectedPreset] = useState("");

    const serverType = props.server.type as ServerType;

    const {data: serverData, isLoading: serverLoading} = useServer(props.server.id, {enabled: isOpen});
    const {data: modsData = [], isLoading: modsLoading} = useMods(serverType, {enabled: isOpen});
    const {data: presetsData = [], isLoading: presetsLoading} = usePresets(serverType, {enabled: isOpen});

    const isLoading = isOpen && (serverLoading || modsLoading || presetsLoading);

    useEffect(() => {
        if (!isOpen || !serverData || modsLoading || presetsLoading) return;
        const activeMods: ServerWorkshopModDto[] = (serverData as Arma3ServerDto | DayZServerDto).activeMods ?? [];
        setSelectedMods(activeMods);
        setAvailableMods(
            (modsData as unknown as ServerWorkshopModDto[])
                .filter(mod => !activeMods.find(m => m.id === mod.id))
                .sort((a, b) => (a.name ?? "").localeCompare(b.name ?? ""))
        );
        setSelectedPreset("");
    }, [isOpen, serverData, modsLoading, presetsLoading]);

    const serverRunning = props.status != null && props.status.alive;

    function handleManageModsButtonClick() {
        if (props.server.id === undefined) {
            return;
        }
        setIsOpen(true);
    }

    function handleModSelect(option: ServerWorkshopModDto) {
        setSelectedPreset("");
        setAvailableMods((prevState) => prevState.filter(item => item !== option));
        setSelectedMods((prevState) =>
            [option, ...prevState].sort((a, b) => (a.name ?? "").localeCompare(b.name ?? ""))
        );
    }

    function handleModDeselect(option: ServerWorkshopModDto) {
        setSelectedPreset("");
        setSelectedMods((prevState) => prevState.filter(item => item !== option));
        setAvailableMods((prevState) =>
            [option, ...prevState].sort((a, b) => (a.name ?? "").localeCompare(b.name ?? ""))
        );
    }

    function handlePresetChange(e: SelectChangeEvent) {
        const presetId = Number(e.target.value);
        const preset = presetsData.find((preset: PresetResponseDto) => preset.id === presetId);
        if (!preset) {
            return;
        }

        const newAvailableMods = [...availableMods, ...selectedMods];
        const newSelectedMods = [];
        for (const mod of preset.mods ?? []) {
            const selectedMod = newAvailableMods.find(m => m.id === mod.id);
            if (!selectedMod) {
                continue;
            }
            const index = newAvailableMods.indexOf(selectedMod);
            newSelectedMods.push(selectedMod);
            newAvailableMods.splice(index, 1);
        }

        setAvailableMods(newAvailableMods);
        setSelectedMods(newSelectedMods);
        setSelectedPreset(String(presetId));
    }

    async function handleConfirm() {
        if (props.server.id === undefined) {
            return;
        }

        setIsOpen(false);
        try {
            await serversApi.updateServer({
                id: props.server.id,
                serverDto: {...serverData, activeMods: selectedMods} as unknown as ServerDto
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
                                 onSelect={handleModSelect} onDeselect={handleModDeselect}
                                 itemsLabel="mods" showFilter selectedPreset={selectedPreset} presets={presetsData}
                                 onPresetChange={handlePresetChange} withControls
                                 onConfirm={handleConfirm} onCancel={handleClose}
                                 confirmDisabled={serverRunning}
                    />
                </Box>
            </Modal>
        </>
    );
};

export default ListBuilderModEdit;
