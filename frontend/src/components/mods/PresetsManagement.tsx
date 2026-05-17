import {ChangeEvent, useEffect, useState} from "react";
import Table from "@mui/material/Table";
import TableContainer from "@mui/material/TableContainer";
import Paper from "@mui/material/Paper";
import TableCell from "@mui/material/TableCell";
import TableRow from "@mui/material/TableRow";
import TableHead from "@mui/material/TableHead";
import TableBody from "@mui/material/TableBody";
import {Backdrop, Box, CircularProgress, Divider, Menu, MenuItem, Modal, Stack, Toolbar} from "@mui/material";
import Typography from "@mui/material/Typography";
import IconButton from '@mui/material/IconButton';
import EditIcon from '@mui/icons-material/Edit';
import MoreVertIcon from '@mui/icons-material/MoreVert';
import ListItemIcon from '@mui/material/ListItemIcon';
import DriveFileRenameOutlineIcon from '@mui/icons-material/DriveFileRenameOutline';
import DownloadIcon from '@mui/icons-material/Download';
import DeleteIcon from '@mui/icons-material/Delete';
import InfoOutlinedIcon from '@mui/icons-material/InfoOutlined';
import SERVER_NAMES from "../../util/serverNames";
import {humanFileSize} from "../../util/util";
import {toast} from "react-toastify";
import ListBuilder from "../../UI/ListBuilder/ListBuilder";
import TableGhosts from "../../UI/TableSkeletons";
import Tooltip from "@mui/material/Tooltip";
import UploadIcon from '@mui/icons-material/Upload';
import {ModDto, PresetResponseDto, PresetResponseModDto, ServerType} from "../../api/generated";
import {modPresetsApi, modsApi, armaLauncherPresetApi} from "../../api/client";
import {downloadExportedPreset} from "../../api/downloads";
import ImportPresetDialog from "./ImportPresetDialog";
import RenamePresetDialog from "./RenamePresetDialog";

export default function PresetsManagement() {
    const [initialLoading, setInitialLoading] = useState(true);
    const [presets, setPresets] = useState<Array<PresetResponseDto>>([]);
    const [editedPreset, setEditedPreset] = useState<PresetResponseDto | null>();
    const [presetModalOpen, setPresetModalOpen] = useState(false);
    const [selectedMods, setSelectedMods] = useState<Array<PresetResponseModDto>>([]);
    const [availableMods, setAvailableMods] = useState<Array<PresetResponseModDto>>([]);
    const [isUploadInProgress, setIsUploadInProgress] = useState(false);
    const [importDialogOpen, setImportDialogOpen] = useState(false);
    const [importFile, setImportFile] = useState<File | null>(null);
    const [renameDialogOpen, setRenameDialogOpen] = useState(false);
    const [renamePresetId, setRenamePresetId] = useState<number | null>(null);
    const [menuAnchor, setMenuAnchor] = useState<{el: HTMLElement, presetId: number} | null>(null);

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

    function handleFileInput(e: ChangeEvent<HTMLInputElement>) {
        if (!e.target.files?.[0]) {
            return;
        }
        setImportFile(e.target.files[0]);
        setImportDialogOpen(true);
        e.target.value = '';
    }

    async function handleImportConfirmed(name: string) {
        if (!importFile) {
            return;
        }
        try {
            setIsUploadInProgress(true);
            setImportDialogOpen(false);
            const {data: importedPreset} = await armaLauncherPresetApi.importLauncherPreset({preset: importFile, name});
            setPresets(prevState => [...prevState, importedPreset]);
            setIsUploadInProgress(false);
            toast.success(`Preset '${importedPreset.name}' successfully imported`);
        } catch (e: any) {
            setIsUploadInProgress(false);
            console.error(e);
            toast.error(e.response.data || "Error during mod import");
        } finally {
            setImportFile(null);
        }
    }

    function handleRenameClick(id: number) {
        setRenamePresetId(id);
        setRenameDialogOpen(true);
    }

    async function handleRenameConfirmed(name: string) {
        if (renamePresetId === null) {
            return;
        }
        try {
            const {data: renamedPreset} = await modPresetsApi.renamePreset({
                id: renamePresetId,
                renamePresetRequestDto: {name}
            });
            setPresets(prevState => {
                const updated = [...prevState];
                const old = updated.find(p => p.id === renamedPreset.id);
                return [...updated.filter(p => p !== old), renamedPreset];
            });
            setRenameDialogOpen(false);
            setRenamePresetId(null);
            toast.success(`Preset successfully renamed to '${renamedPreset.name}'`);
        } catch (e: any) {
            console.error(e);
            toast.error(e.response.data || "Could not rename preset");
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

    function handleMenuOpen(event: React.MouseEvent<HTMLElement>, presetId: number) {
        setMenuAnchor({el: event.currentTarget, presetId});
    }

    function handleMenuClose() {
        setMenuAnchor(null);
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
                                    <TableCell sx={{width: '20%'}}>Name</TableCell>
                                    <TableCell sx={{width: '10%', whiteSpace: 'nowrap'}}>Type</TableCell>
                                    <TableCell sx={{width: '20%'}}>Mods</TableCell>
                                    <TableCell sx={{width: '8%', whiteSpace: 'nowrap'}}>Total size</TableCell>
                                    <TableCell sx={{width: '2%'}}></TableCell>
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
                                            <Tooltip title={
                                                <>{(preset.mods ?? []).map(m => <div key={m.id}>{m.name}</div>)}</>
                                            } placement="bottom-start">
                                                <Stack direction="row" spacing={0.5} sx={{width: 'fit-content', cursor: 'default', alignItems: "center"}}>
                                                    <span>{(preset.mods ?? []).length} mods total</span>
                                                    <InfoOutlinedIcon sx={{fontSize: 14, color: 'text.secondary'}}/>
                                                </Stack>
                                            </Tooltip>
                                        </TableCell>
                                        <TableCell>
                                            {preset.totalModsSize != null ? humanFileSize(preset.totalModsSize) : '—'}
                                        </TableCell>
                                        <TableCell align="right" sx={{whiteSpace: 'nowrap', pr: 1}}>
                                            <Tooltip title="Edit mods">
                                                <IconButton size="small" aria-label="edit" data-testid={`preset-edit-${preset.id}`}
                                                            onClick={() => handleOpenEdit(preset)}>
                                                    <EditIcon fontSize="small" color="primary"/>
                                                </IconButton>
                                            </Tooltip>
                                            <IconButton size="small" aria-label="more actions" data-testid={`preset-menu-${preset.id}`}
                                                        onClick={(e) => handleMenuOpen(e, preset.id!)}>
                                                <MoreVertIcon fontSize="small"/>
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
            <ImportPresetDialog
                open={importDialogOpen}
                file={importFile}
                existingPresetNames={presets.map(p => p.name ?? '')}
                onConfirmClicked={handleImportConfirmed}
                onClose={() => {
                    setImportDialogOpen(false);
                    setImportFile(null);
                }}
            />
            <RenamePresetDialog
                open={renameDialogOpen}
                currentName={presets.find(p => p.id === renamePresetId)?.name ?? ''}
                existingPresetNames={presets.filter(p => p.id !== renamePresetId).map(p => p.name ?? '')}
                onConfirmClicked={handleRenameConfirmed}
                onClose={() => {
                    setRenameDialogOpen(false);
                    setRenamePresetId(null);
                }}
            />
            <Menu
                anchorEl={menuAnchor?.el}
                open={menuAnchor !== null}
                onClose={handleMenuClose}
            >
                <MenuItem data-testid={`preset-menu-edit-${menuAnchor?.presetId}`} onClick={() => {
                    handleMenuClose();
                    const preset = presets.find(p => p.id === menuAnchor?.presetId);
                    if (preset) handleOpenEdit(preset);
                }}>
                    <ListItemIcon><EditIcon fontSize="small"/></ListItemIcon>
                    Edit mods
                </MenuItem>
                <MenuItem data-testid={`preset-menu-rename-${menuAnchor?.presetId}`} onClick={() => {
                    const id = menuAnchor?.presetId;
                    handleMenuClose();
                    if (id !== undefined) handleRenameClick(id);
                }}>
                    <ListItemIcon><DriveFileRenameOutlineIcon fontSize="small"/></ListItemIcon>
                    Rename
                </MenuItem>
                <MenuItem data-testid={`preset-menu-download-${menuAnchor?.presetId}`} onClick={() => {
                    const id = menuAnchor?.presetId;
                    handleMenuClose();
                    if (id !== undefined) handleDownload(id);
                }}>
                    <ListItemIcon><DownloadIcon fontSize="small"/></ListItemIcon>
                    Download
                </MenuItem>
                <MenuItem data-testid={`preset-menu-delete-${menuAnchor?.presetId}`} onClick={() => {
                    const id = menuAnchor?.presetId;
                    handleMenuClose();
                    if (id !== undefined) handleDelete(id);
                }} sx={{color: 'error.main'}}>
                    <ListItemIcon><DeleteIcon fontSize="small" color="error"/></ListItemIcon>
                    Delete
                </MenuItem>
            </Menu>
        </>
    )
}
