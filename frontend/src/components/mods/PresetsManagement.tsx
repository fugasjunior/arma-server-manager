import {ChangeEvent, useEffect, useState} from "react";
import {
    deleteModPreset,
    downloadExportedPreset,
    getModPresets,
    updateModPreset,
    uploadImportedPreset
} from "../../services/modPresetsService";
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
import SERVER_NAMES from "../../util/serverNames";
import {toast} from "material-react-toastify";
import ListBuilder from "../../UI/ListBuilder/ListBuilder";
import {getMods} from "../../services/modsService";
import TableGhosts from "../../UI/TableSkeletons";
import Tooltip from "@mui/material/Tooltip";
import UploadIcon from '@mui/icons-material/Upload';
import DownloadIcon from '@mui/icons-material/Download';
import {ModPresetDto, ModPresetModDto} from "../../dtos/ModPresetDto";
import {ModDto} from "../../dtos/ModDto.ts";
import {ServerType} from "../../dtos/ServerDto.ts";
import DriveFileRenameOutlineOutlinedIcon from '@mui/icons-material/DriveFileRenameOutlineOutlined';
import RenamePresetDialog from "./RenamePresetDialog";
import MemoryIcon from "@mui/icons-material/Memory";
import {renameModPreset} from "../../services/modPresetsService";

export default function PresetsManagement() {
    const [initialLoading, setInitialLoading] = useState(true);
    const [presets, setPresets] = useState<Array<ModPresetDto>>([]);
    const [editedPreset, setEditedPreset] = useState<ModPresetDto | null>();
    const [presetModalOpen, setPresetModalOpen] = useState(false);
    const [selectedMods, setSelectedMods] = useState<Array<ModPresetModDto>>([]);
    const [availableMods, setAvailableMods] = useState<Array<ModPresetModDto>>([]);
    const [isUploadInProgress, setIsUploadInProgress] = useState(false);
    const [renamePresetDialogOpen, setRenamePresetDialogOpen] = useState(false);
    const [selectedPresetId, setSelectedPresetId] = useState<string | null>(null);

    useEffect(() => {
        async function fetchPresets() {
            const {data: presetsDto} = await getModPresets();
            setPresets(presetsDto.presets);
            setInitialLoading(false);
        }

        fetchPresets();
    }, []);

    async function handleDelete(id: string) {
        const deletedPreset = presets.find(preset => preset.id === id);
        if (!deletedPreset) {
            return;
        }

        setPresets(prevState => [...prevState].filter(preset => preset !== deletedPreset));

        try {
            await deleteModPreset(id);
            toast.success(`Preset '${deletedPreset.name}' successfully deleted`);
        } catch (e: any) {
            console.error(e);
            toast.error(e.response.data || "Could not delete preset");
            setPresets(prevState => [...prevState, deletedPreset]);
        }
    }

    function getSummarizedModsList(mods: Array<ModPresetModDto>) {
        const CUTOFF_LENGTH = 55;
        const modNames = mods.map(mod => mod.shortName);
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
        return presets.sort((a, b) => a.name.localeCompare(b.name));
    }

    async function handleOpenEdit(preset: ModPresetDto) {
        const {data: modsDto} = await getMods(preset.type);
        let availableMods = modsDto.workshopMods;
        availableMods = availableMods.filter((mod: ModDto) => !preset.mods.find(searchedMod => searchedMod.id === mod.id))

        setEditedPreset(preset);
        setSelectedMods(preset.mods);
        setAvailableMods(availableMods);
        setPresetModalOpen(true);
    }

    async function handlePresetModalClosed() {
        if (!editedPreset || editedPreset.id === undefined) {
            return;
        }

        const request = {
            name: editedPreset.name,
            mods: selectedMods.map(mod => mod.id)
        }

        if (selectedMods.length === 0) {
            await handleDelete(editedPreset.id);
        } else {
            try {
                const {data: savedPreset} = await updateModPreset(editedPreset.id, request);
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

    function handleModSelect(option: ModPresetModDto) {

        setAvailableMods((prevState) => {
            return prevState.filter(item => item !== option);
        });
        setSelectedMods((prevState) => {
            return [option, ...prevState].sort((a, b) => a.name.localeCompare(b.name));
        });
    }

    function handleModDeselect(option: ModPresetModDto) {
        setSelectedMods((prevState) => {
            return prevState.filter(item => item !== option);
        });

        setAvailableMods((prevState) => {
            return [option, ...prevState].sort((a, b) => a.name.localeCompare(b.name));
        });
    }

    async function handleFileInput(e: ChangeEvent<HTMLInputElement>) {
        if (!e.target.files) {
            return;
        }

        try {
            setIsUploadInProgress(true);
            const {data: importedPreset} = await uploadImportedPreset(e.target.files[0]);
            setPresets(prevState => [...prevState, importedPreset]);
            setIsUploadInProgress(false);
            toast.success(`Preset '${importedPreset.name}' successfully imported`);
        } catch (e: any) {
            setIsUploadInProgress(false);
            console.error(e);
            toast.error(e.response.data || "Error during mod import");
        }
    }

    function handleDownload(presetId: string) {
        try {
            downloadExportedPreset(presetId);
        } catch (e: any) {
            console.error(e);
            toast.error(e.response.data || "Error during mod export");
        }
    }

    const handlePresedDialogOpen = (presetId: string) => {
        setSelectedPresetId(presetId);
        setRenamePresetDialogOpen(true);
    }

    const handlePresedDialogClose = () => {
        setRenamePresetDialogOpen(false);
        setSelectedPresetId(null);
    }

    const handleRenameNewPreset = async (presetName: string, presetId: string) => {
        setRenamePresetDialogOpen(false);
        setSelectedPresetId(null);

        const request = {
            name: presetName,
        }

        await renameModPreset(presetId, request);
        toast.success(`Preset '${presetName}' successfully renamed`);
        window.location.reload();
    }

    return (
        <>
            <Backdrop open={isUploadInProgress}>
                <CircularProgress color="inherit"/>
            </Backdrop>
            <input type="file" id="fileInput" onChange={handleFileInput} style={{display: 'none'}}/>
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
                                        sx={{'&:last-child td, &:last-child th': {border: 0}}}
                                    >
                                        <TableCell component="th" scope="row">
                                            {preset.name}
                                        </TableCell>
                                        <TableCell>
                                            {SERVER_NAMES.get(ServerType[preset.type as keyof typeof ServerType])}
                                        </TableCell>
                                        <TableCell>
                                            {getSummarizedModsList(preset.mods)}
                                        </TableCell>
                                        <TableCell align="right">
                                            {preset.mods.length}
                                        </TableCell>
                                        <TableCell>
                                            <IconButton aria-label="edit"
                                                        onClick={() => handleOpenEdit(preset)}>
                                                <MemoryIcon color="primary"/>
                                            </IconButton>
                                        </TableCell>
                                        <TableCell>
                                            <IconButton aria-label="rename"
                                                        onClick={() => handlePresedDialogOpen(preset.id as string)}>
                                                <DriveFileRenameOutlineOutlinedIcon color="primary"/>
                                            </IconButton>
                                        </TableCell>
                                        <TableCell>
                                            <IconButton aria-label="export"
                                                        onClick={() => handleDownload(preset.id as string)}>
                                                <DownloadIcon color="primary"/>
                                            </IconButton>
                                        </TableCell>
                                        <TableCell>
                                            <IconButton aria-label="delete"
                                                        onClick={() => handleDelete(preset.id as string)}>
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
                    <ListBuilder selectedOptions={selectedMods} availableOptions={availableMods}
                                 onSelect={handleModSelect} onDeselect={handleModDeselect}
                                 itemsLabel="mods" showFilter
                    />
                </Box>
            </Modal>
            <RenamePresetDialog open={renamePresetDialogOpen} onClose={handlePresedDialogClose}
                                onConfirmClicked={handleRenameNewPreset} presetId={selectedPresetId}
            />
        </>
    )
}