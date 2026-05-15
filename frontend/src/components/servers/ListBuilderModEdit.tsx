import {useState} from "react";
import {Backdrop, Box, Button, CircularProgress, Modal, SelectChangeEvent} from "@mui/material";
import ListBuilder from "../../UI/ListBuilder/ListBuilder";
import {serversApi, modsApi, modPresetsApi} from "../../api/client";
import {PresetResponseDto, ServerDto, ServerInstanceInfoDto, ServerType, ServerWorkshopModDto} from "../../api/generated";
import {Arma3ServerDto, DayZServerDto} from "../../api/serverModels";
import {toast} from "material-react-toastify";
import MemoryIcon from "@mui/icons-material/Memory";

type ListBuilderModEditProps = {
    server: ServerDto
    status: ServerInstanceInfoDto | null
}

const ListBuilderModEdit = (props: ListBuilderModEditProps) => {
    const [server, setServer] = useState<ServerDto>();
    const [isOpen, setIsOpen] = useState(false);
    const [availableMods, setAvailableMods] = useState<Array<ServerWorkshopModDto>>([]);
    const [selectedMods, setSelectedMods] = useState<Array<ServerWorkshopModDto>>([]);
    const [presets, setPresets] = useState<Array<PresetResponseDto>>([]);
    const [selectedPreset, setSelectedPreset] = useState("");
    const [isLoading, setIsLoading] = useState(false);

    const serverRunning = props.status != null && props.status.alive;

    async function handleManageModsButtonClick() {
        if (props.server.id === undefined) {
            return;
        }

        setIsLoading(true);
        setIsOpen(false);
        try {
            const {data: presetsDto} = await modPresetsApi.getPresets({filter: props.server.type as ServerType});
            const {data: serverDto} = await serversApi.getServer({id: props.server.id});
            const {data: modsDto} = await modsApi.getMods({filter: props.server.type as ServerType});
            setPresets(presetsDto.presets ?? []);
            setServer(serverDto);
            setSelectedMods((serverDto as Arma3ServerDto | DayZServerDto).activeMods ?? []);
            setAvailableMods((modsDto.workshopMods ?? []).filter((mod: ServerWorkshopModDto) => !((serverDto as Arma3ServerDto | DayZServerDto).activeMods ?? [])
                .find((searchedMod: ServerWorkshopModDto) => searchedMod.id === mod.id))
                .sort((a: ServerWorkshopModDto, b: ServerWorkshopModDto) => (a.name ?? "").localeCompare(b.name ?? "")));
            setSelectedPreset("");
            setIsOpen(true);
        } catch (e: any) {
            console.error(e);
            toast.error(e.response.data || "Could not load server data");
        }
        setIsLoading(false);
    }

    function handleModSelect(option: ServerWorkshopModDto) {
        setSelectedPreset("");

        setAvailableMods((prevState) => {
            return prevState.filter(item => item !== option);
        });

        setSelectedMods((prevState) => {
            return [option, ...prevState].sort((a, b) => (a.name ?? "").localeCompare(b.name ?? ""));
        });
    }

    function handleModDeselect(option: ServerWorkshopModDto) {
        setSelectedPreset("");

        setSelectedMods((prevState) => {
            return prevState.filter(item => item !== option);
        });

        setAvailableMods((prevState) => {
            return [option, ...prevState].sort((a, b) => (a.name ?? "").localeCompare(b.name ?? ""));
        });
    }

    function handlePresetChange(e: SelectChangeEvent) {
        const presetId = Number(e.target.value);
        const preset = presets.find(preset => preset.id === presetId);
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
            console.log(mod, selectedMod);
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
            await serversApi.updateServer({id: props.server.id, serverDto: {...server, activeMods: selectedMods} as unknown as ServerDto});
            toast.success("Mods successfully set");
        } catch (e: any) {
            console.error(e);
            toast.error(e.data.response || "Failed to update the server");
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
            <Modal open={isOpen} onClose={handleClose}>
                <Box>
                    <ListBuilder selectedOptions={selectedMods} availableOptions={availableMods}
                                 onSelect={handleModSelect} onDeselect={handleModDeselect}
                                 itemsLabel="mods" showFilter selectedPreset={selectedPreset} presets={presets}
                                 onPresetChange={handlePresetChange} withControls
                                 onConfirm={handleConfirm} onCancel={handleClose}
                                 confirmDisabled={serverRunning}
                    />
                </Box>
            </Modal>
        </>
    )
}

export default ListBuilderModEdit;
