import {ChangeEvent, useEffect, useState} from "react";
import Table from "@mui/material/Table";
import TableContainer from "@mui/material/TableContainer";
import Paper from "@mui/material/Paper";
import TableCell from "@mui/material/TableCell";
import TableRow from "@mui/material/TableRow";
import TableHead from "@mui/material/TableHead";
import TableBody from "@mui/material/TableBody";
import {Backdrop, Box, CircularProgress, Divider, Modal, Stack, Toolbar} from "@mui/material";
import Typography from "@mui/material/Typography";
import IconButton from '@mui/material/IconButton';
import DeleteIcon from '@mui/icons-material/Delete';
import EditIcon from '@mui/icons-material/Edit';
import SERVER_NAMES from "../../util/serverNames";
import {toast} from "material-react-toastify";
import ListBuilder from "../../UI/ListBuilder/ListBuilder";
import TableGhosts from "../../UI/TableSkeletons";
import Tooltip from "@mui/material/Tooltip";
import UploadIcon from '@mui/icons-material/Upload';
import DownloadIcon from '@mui/icons-material/Download';
import {ModDto, PresetResponseDto, PresetResponseModDto, ServerType} from "../../api/generated";
import {modPresetsApi, modsApi, armaLauncherPresetApi} from "../../api/client";
import {downloadExportedPreset} from "../../api/downloads";

export default function PresetsManagement() {
    const [initialLoading, setInitialLoading] = useState(true);
    const [presets, setPresets] = useState<Array<PresetResponseDto>>([]);
    const [editedPreset, setEditedPreset] = useState<PresetResponseDto | null>();
    const [presetModalOpen, setPresetModalOpen] = useState(false);
    const [selectedMods, setSelectedMods] = useState<Array<PresetResponseModDto>>([]);
    const [availableMods, setAvailableMods] = useState<Array<PresetResponseModDto>>([]);
    const [isUploadInProgress, setIsUploadInProgress] = useState(false);

    useEffect(() => {
        async function fetchPresets() {
            const {data: presetsDto} = await modPresetsApi.getPresets();
            setPresets(presetsDto.presets ?? []);
            setInitialLoading(false);
        }

        fetchPresets();
    }, []);

    async function handleDelete(id: number) {
        const deletedPreset = presets.find(preset => preset.id === id);
        if (!deletedPreset) {
            return;
        }

        setPresets(prevState => [...prevState].filter(preset => preset !== deletedPreset));

        try {
            await modPresetsApi.deletePreset({id});
            toast.success(`Preset '${deletedPreset.name}' successfully deleted`);
        } catch (e: any) {
            console.error(e);
            toast.error(e.response.data || "Could not delete preset");
            setPresets(prevState => [...prevState, deletedPreset]);
        }
    }

    function getSummarizedModsList(mods: Array<PresetResponseModDto>) {
        const CUTOFF_LENGTH = 55;
        const modNames = mods.map(mod => mod.shortName ?? "");
        modNames.sort((a, b) => a.localeCompare(b));
        let summarizedList = modNames[0];
        let i = 1;
        while (summarizedList.length < CUTOFF_LENGTH && i < modNames.length) {
            summarizedList += ", " + modNames[i++];
        }
        if (summarizedList.length >= CUTOFF_LENGTH) {
            summarizedList += " and " + (modNames.length - i) + " more...";
        }
        return summarizedList;
    }

    function getSortedPresets() {
        return presets.sort((a, b) => (a.name ?? "").localeCompare(b.name ?? ""));
    }

    async function handleOpenEdit(preset: PresetResponseDto) {
        const {data: modsDto} = await modsApi.getMods({filter: preset.type as ServerType});
        let available = modsDto.workshopMods ?? [];
        available = available.filter((mod: ModDto) => !(preset.mods ?? []).find(searchedMod => searchedMod.id === mod.id))

        setEditedPreset(preset);
        setSelectedMods(preset.mods ?? []);
        setAvailableMods(available);
        setPresetModalOpen(true);
    }

    async function handlePresetModalClosed() {
        if (!editedPreset || editedPreset.id === undefined) {
            return;
        }

        const request = {
            name: editedPreset.name!,
            mods: selectedMods.map(mod => mod.id!)
        }

        if (selectedMods.length === 0) {
            await handleDelete(editedPreset.id);
        } else {
            try {
                const {data: savedPreset} = await modPresetsApi.updatePreset({id: editedPreset.id, updatePresetRequestDto: request});
                setPresets(prevState => {
                    const newState = [...prevState];
                    const oldPreset = newState.find(preset => preset.id === savedPreset.id);
                    return [...newState.filter(preset => preset !== oldPreset), savedPreset];
                });
                toast.success(`Preset '${savedPreset.name}' successfully updated`);
            } catch (e: any) {
                console.error(e);
                toast.error(e.response.data || "Could not update preset");
            }
        }

        setEditedPreset(null);
        setSelectedMods([]);
        setAvailableMods([]);
        setPresetModalOpen(false);
    }

    function handleModSelect(option: PresetResponseModDto) {
        setAvailableMods((prevState) => {
            return prevState.filter(item => item !== option);
        });
        setSelectedMods((prevState) => {
            return [option, ...prevState].sort((a, b) => (a.name ?? "").localeCompare(b.name ?? ""));
        });
    }

    function handleModDeselect(option: PresetResponseModDto) {
        setSelectedMods((prevState) => {
            return prevState.filter(item => item !== option);
        });

        setAvailableMods((prevState) => {
            return [option, ...prevState].sort((a, b) => (a.name ?? "").localeCompare(b.name ?? ""));
        });
    }

    async function handleFileInput(e: ChangeEvent<HTMLInputElement>) {
        if (!e.target.files) {
            return;
        }

        try {
            setIsUploadInProgress(true);
            const {data: importedPreset} = await armaLauncherPresetApi.importLauncherPreset({preset: e.target.files[0]});
            setPresets(prevState => [...prevState, importedPreset]);
            setIsUploadInProgress(false);
            toast.success(`Preset '${importedPreset.name}' successfully imported`);
        } catch (e: any) {
            setIsUploadInProgress(false);
            console.error(e);
            toast.error(e.response.data || "Error during mod import");
        }
    }

    function handleDownload(presetId: number) {
        try {
            downloadExportedPreset(presetId);
        } catch (e: any) {
            console.error(e);
            toast.error(e.response.data || "Error during mod export");
        }
    }

    return (
        <>
            <Backdrop open={isUploadInProgress}>
                <CircularProgress color="inherit"/>
            </Backdrop>
            <input type="file" id="fileInput" data-testid="preset-import-input" onChange={handleFileInput} style={{display: 'none'}}/>
            <Box sx={{width: '100%'}}>
                <Paper sx={{width: '100%'}}>
                    <Toolbar sx={{pl: {sm: 2}, pr: {xs: 1, sm: 1}}}>
                        <Typography sx={{flex: '1 1 100%'}} variant="h6" id="tableTitle" component="div">
                            Presets
                        </Typography>
                        <Stack direction="row" spacing={2} divider={<Divider orientation={"vertical"} flexItem/>}>
                            <Tooltip title="Import preset">
                                <label htmlFor="fileInput">
                                    <IconButton component="span">
                                        <UploadIcon/>
                                    </IconButton>
                                </label>
                            </Tooltip>
                        </Stack>
                    </Toolbar>
                    <TableContainer component={Paper}>
                        <Table sx={{minWidth: 650}} aria-label="simple table">
                            <TableHead>
                                <TableRow>
                                    <TableCell>Name</TableCell>
                                    <TableCell>Type</TableCell>
                                    <TableCell>Mods</TableCell>
                                    <TableCell align="right">Mods count</TableCell>
                                    <TableCell></TableCell>
                                    <TableCell></TableCell>
                                    <TableCell></TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {getSortedPresets().map((preset) => (
                                    <TableRow
                                        key={preset.id}
                                        data-testid={`preset-row-${preset.id}`}
                                        sx={{'&:last-child td, &:last-child th': {border: 0}}}
                                    >
                                        <TableCell component="th" scope="row">
                                            {preset.name}
                                        </TableCell>
                                        <TableCell>
                                            {SERVER_NAMES.get(preset.type as ServerType)}
                                        </TableCell>
                                        <TableCell>
                                            {getSummarizedModsList(preset.mods ?? [])}
                                        </TableCell>
                                        <TableCell align="right">
                                            {(preset.mods ?? []).length}
                                        </TableCell>
                                        <TableCell>
                                            <IconButton aria-label="edit" data-testid={`preset-edit-${preset.id}`}
                                                        onClick={() => handleOpenEdit(preset)}>
                                                <EditIcon color="primary"/>
                                            </IconButton>
                                        </TableCell>
                                        <TableCell>
                                            <IconButton aria-label="export" data-testid={`preset-export-${preset.id}`}
                                                        onClick={() => handleDownload(preset.id!)}>
                                                <DownloadIcon color="primary"/>
                                            </IconButton>
                                        </TableCell>
                                        <TableCell>
                                            <IconButton aria-label="delete" data-testid={`preset-delete-${preset.id}`}
                                                        onClick={() => handleDelete(preset.id!)}>
                                                <DeleteIcon color="error"/>
                                            </IconButton>
                                        </TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                        <TableGhosts display={initialLoading} count={10}/>
                    </TableContainer>
                </Paper>
            </Box>
            <Modal open={presetModalOpen} onClose={handlePresetModalClosed}>
                <Box>
                    <ListBuilder selectedOptions={selectedMods as any} availableOptions={availableMods as any}
                                 onSelect={handleModSelect as any} onDeselect={handleModDeselect as any}
                                 itemsLabel="mods" showFilter
                    />
                </Box>
            </Modal>
        </>
    )
}
