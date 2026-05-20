import {ChangeEvent, useEffect, useState} from "react";
import {Backdrop, Box, Button, CircularProgress, Modal, Stack, TextField} from "@mui/material";
import {serversApi} from "../../api/client";
import {ServerDto, ServerInstanceInfoDto} from "../../api/generated";
import {ReforgerServerDto} from "../../api/serverModels";
import {toast} from "react-toastify";
import AddIcon from "@mui/icons-material/Add";
import TableContainer from "@mui/material/TableContainer";
import Table from "@mui/material/Table";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import TableCell from "@mui/material/TableCell";
import TableBody from "@mui/material/TableBody";
import IconButton from "@mui/material/IconButton";
import DeleteIcon from "@mui/icons-material/Delete";
import MemoryIcon from '@mui/icons-material/Memory';
import {useServer} from "../../hooks/queries/useServer";
import {useQueryClient} from "@tanstack/react-query";
import {queryKeys} from "../../api/queryKeys";
import {usePermission} from "../../hooks/usePermission";

const modalStyle = {
    position: 'absolute',
    top: '50%',
    left: '50%',
    transform: 'translate(-50%, -50%)',
    width: 600,
    bgcolor: 'background.paper',
    border: '2px solid #000',
    boxShadow: 24,
    p: 4,
};

type ReforgerModEditProps = {
    server: ReforgerServerDto,
    serverStatus: ServerInstanceInfoDto | null
}

const ReforgerModEdit = (props: ReforgerModEditProps) => {
    const queryClient = useQueryClient();
    const canModify = usePermission('MOD_MODIFY');
    const [isOpen, setIsOpen] = useState(false);
    const [mods, setMods] = useState<Array<{id: string, name: string}>>(props.server.activeMods ?? []);
    const [newModName, setNewModName] = useState("");
    const [newModId, setNewModId] = useState("");

    const {data: serverData, isLoading} = useServer(props.server.id, {enabled: isOpen});

    useEffect(() => {
        if (!isOpen || !serverData) return;
        setMods((serverData as ReforgerServerDto).activeMods ?? []);
    }, [isOpen, serverData]);

    const serverRunning = props.serverStatus != null && props.serverStatus.alive;

    function handleManageModsButtonClick() {
        if (props.server.id === undefined) return;
        setIsOpen(true);
    }

    function handleNewModNameChange(event: ChangeEvent<HTMLInputElement>) {
        setNewModName(event.target.value);
    }

    function handleNewModIdChange(event: ChangeEvent<HTMLInputElement>) {
        setNewModId(event.target.value);
    }

    function handleAddNewMod() {
        if (!mods.find(mod => mod.id === newModId)) {
            setMods(prevState => [...prevState, {id: newModId, name: newModName}]);
        }
        setNewModId("");
        setNewModName("");
    }

    function handleDeleteMod(id: string) {
        setMods(prevState => prevState.filter(mod => mod.id !== id));
    }

    async function handleConfirm() {
        if (props.server.id === undefined) return;

        setIsOpen(false);
        try {
            await serversApi.updateServer({
                id: props.server.id,
                serverDto: {...serverData, activeMods: mods} as unknown as ServerDto
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
            <Backdrop open={isOpen && isLoading}>
                <CircularProgress color="inherit"/>
            </Backdrop>
            <Button onClick={handleManageModsButtonClick} startIcon={<MemoryIcon/>} variant="contained">
                Mods
            </Button>
            <Modal open={isOpen && !isLoading} onClose={handleClose}>
                <Box sx={modalStyle}>
                    <Stack direction="row" spacing={1} sx={{mb: 2, justifyItems: "center", justifyContent: "space-between"}}>
                        <TextField id="mod-id" label="Mod ID" placeholder="Mod ID" size="small" required
                                   variant="standard" value={newModId} onChange={handleNewModIdChange}
                                   disabled={!canModify}/>
                        <TextField id="mod-name" label="Mod name" placeholder="Mod name" size="small" required
                                   variant="standard" value={newModName} onChange={handleNewModNameChange}
                                   disabled={!canModify}/>
                        <Button variant="contained" startIcon={<AddIcon/>} color="success"
                                onClick={handleAddNewMod}
                                disabled={!canModify || newModId.length === 0 || newModName.length === 0}
                        >
                            Add
                        </Button>
                    </Stack>
                    {mods.length > 0 &&
                        <Box sx={{overflow: "auto", maxHeight: 400}}>
                            <TableContainer>
                                <Table size="small">
                                    <TableHead>
                                        <TableRow>
                                            <TableCell>ID</TableCell>
                                            <TableCell>Name</TableCell>
                                            <TableCell></TableCell>
                                        </TableRow>
                                    </TableHead>
                                    <TableBody>
                                        {mods.map(mod => (
                                            <TableRow key={mod.id} style={{height: 33}}>
                                                <TableCell>{mod.id}</TableCell>
                                                <TableCell>{mod.name}</TableCell>
                                                <TableCell align="right">
                                                    {canModify && <IconButton
                                                        aria-label="delete"
                                                        onClick={() => handleDeleteMod(mod.id)}>
                                                        <DeleteIcon color="error"/>
                                                    </IconButton>}
                                                </TableCell>
                                            </TableRow>
                                        ))}
                                    </TableBody>
                                </Table>
                            </TableContainer>
                        </Box>
                    }
                    <Stack>
                        <Button onClick={handleConfirm} variant="contained" color="primary"
                                sx={{mt: 1, mb: 1}}
                                disabled={serverRunning || !canModify}>Confirm</Button>
                        <Button onClick={handleClose} variant="outlined" color="error">Cancel</Button>
                    </Stack>
                </Box>
            </Modal>
        </>
    );
};

export default ReforgerModEdit;
