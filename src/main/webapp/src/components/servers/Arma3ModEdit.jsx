import React, {useState} from "react";
import {Backdrop, Box, Button, CircularProgress, Modal} from "@mui/material";
import ListBuilder from "../../UI/ListBuilder/ListBuilder";
import {getModPresets} from "../../services/modPresetsService";
import {getServer, updateServer} from "../../services/serversService";
import {getMods} from "../../services/modsService";
import {toast} from "material-react-toastify";

const Arma3ModEdit = props => {
    const [server, setServer] = useState();
    const [isOpen, setIsOpen] = useState(false);
    const [availableMods, setAvailableMods] = useState([]);
    const [selectedMods, setSelectedMods] = useState([]);
    const [presets, setPresets] = useState([]);
    const [selectedPreset, setSelectedPreset] = useState("");
    const [isLoading, setIsLoading] = useState(false);

    async function handleManageModsButtonClick() {
        setIsLoading(true);
        setIsOpen(false);
        try {
            const {data: presetsDto} = await getModPresets("ARMA3");
            const {data: serverDto} = await getServer(props.serverId);
            const {data: modsDto} = await getMods("ARMA3");
            setPresets(presetsDto.presets);
            setServer(serverDto);
            setSelectedMods(serverDto.activeMods);
            setAvailableMods(modsDto.workshopMods.filter(mod => !selectedMods.find(searchedMod => searchedMod.id === mod.id))
                .sort((a, b) => a.name.localeCompare(b.name)));
            setSelectedPreset("");
            setIsOpen(true);
        } catch (e) {
            console.error(e);
            toast.error(e.response.data || "Could not load server data");
        }
        setIsLoading(false);
    }

    function handleModSelect(option) {
        setSelectedPreset("");

        setAvailableMods((prevState) => {
            return prevState.filter(item => item !== option);
        });

        setSelectedMods((prevState) => {
            return [option, ...prevState].sort((a, b) => a.name.localeCompare(b.name));
        });
    }

    function handleModDeselect(option) {
        setSelectedPreset("");

        setSelectedMods((prevState) => {
            return prevState.filter(item => item !== option);
        });

        setAvailableMods((prevState) => {
            return [option, ...prevState].sort((a, b) => a.name.localeCompare(b.name));
        });
    }

    function handlePresetChange(e) {
        const presetId = e.target.value;
        const preset = presets.find(preset => preset.id === presetId);
        if (!preset) {
            return;
        }

        const newAvailableMods = [...availableMods, ...selectedMods];
        const newSelectedMods = [];
        for (const mod of preset.mods) {
            const selectedMod = newAvailableMods.find(m => m.id === mod.id);
            const index = newAvailableMods.indexOf(selectedMod);
            console.log(mod, selectedMod);
            newSelectedMods.push(selectedMod);
            newAvailableMods.splice(index, 1);
        }

        setAvailableMods(newAvailableMods);
        setSelectedMods(newSelectedMods);
        setSelectedPreset(presetId);
    }

    async function handleConfirm() {
        setIsOpen(false);
        try {
            await updateServer(props.serverId, {...server, activeMods: selectedMods});
            toast.success("Mods successfully set");
        } catch (e) {
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
            <Button id="manage-mods-btn" onClick={handleManageModsButtonClick}>Manage mods</Button>
            <Modal open={isOpen} onClose={handleClose}>
                <Box>
                    <ListBuilder selectedOptions={selectedMods} availableOptions={availableMods}
                                 onSelect={handleModSelect} onDeselect={handleModDeselect}
                                 itemsLabel="mods" showFilter selectedPreset={selectedPreset} presets={presets}
                                 onPresetChange={handlePresetChange} withControls
                                 onConfirm={handleConfirm} onCancel={handleClose}
                    />
                </Box>
            </Modal>
        </>
    )
}

export default Arma3ModEdit;
