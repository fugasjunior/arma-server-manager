import {ChangeEvent, useState} from "react";
import {Backdrop, Box, Button, CircularProgress, Modal, Stack, TextField} from "@mui/material";
import {getServer, updateServer} from "../../services/serversService";
import {toast} from "material-react-toastify";
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
import {ReforgerServerDto} from "../../dtos/ServerDto";

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
    server: ReforgerServerDto
}

const ReforgerModEdit = (props: ReforgerModEditProps) => {
    const [server, setServer] = useState<ReforgerServerDto>();
    const [isOpen, setIsOpen] = useState(false);
    const [mods, setMods] = useState(props.server.activeMods);
    const [newModName, setNewModName] = useState("");
    const [newModId, setNewModId] = useState("");
    const [isLoading, setIsLoading] = useState(false);

    const serverRunning = props.server.instanceInfo && props.server.instanceInfo.alive;

    async function handleManageModsButtonClick() {
        if (props.server.id === undefined) {
            return;
        }

        setIsLoading(true);
        setIsOpen(false);
        try {
            const {data: serverDto} = await getServer(props.server.id);
            setServer(serverDto);
            setMods(serverDto.activeMods);
            setIsOpen(true);
        } catch (e: any) {
            console.error(e);
            toast.error(e.response.data || "Could not load server data");
        }
        setIsLoading(false);
    }

    function handleNewModNameChange(event: ChangeEvent<HTMLInputElement>) {
        setNewModName(event.target.value);
    }

    function handleNewModIdChange(event: ChangeEvent<HTMLInputElement>) {
        setNewModId(event.target.value);
    }

    function handleAddNewMod() {
        if (!mods.find(mod => mod.id === Number(newModId))) {
            setMods(prevState => [...prevState, {id: Number(newModId), name: newModName}]);
        }
        setNewModId("");
        setNewModName("");
    }

    function handleDeleteMod(id: number) {
        setMods(prevState => prevState.filter(mod => mod.id !== id));
    }

    async function handleConfirm() {
        if (props.server.id === undefined) {
            return;
        }

        setIsOpen(false);
        try {
            await updateServer(props.server.id, {...server, activeMods: mods});
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
            <Button onClick={handleManageModsButtonClick} startIcon={<MemoryIcon/>}>
                Mods
            </Button>
            <Modal open={isOpen} onClose={handleClose}>
                <Box sx={modalStyle}>
                    <Stack direction="row" spacing={1} mb={2} justifyItems="center" justifyContent="space-between">
                        <TextField id="mod-id" label="Mod ID" placeholder="Mod ID" size="small" required
                                   variant="standard" value={newModId} onChange={handleNewModIdChange}/>
                        <TextField id="mod-name" label="Mod name" placeholder="Mod name" size="small" required
                                   variant="standard" value={newModName} onChange={handleNewModNameChange}/>
                        <Button variant="contained" startIcon={<AddIcon/>} color="success"
                                onClick={handleAddNewMod}
                                disabled={newModId.length === 0 || newModName.length === 0}
                        >
                            Add
                        </Button>
                    </Stack>
                    {mods.length > 0 &&
                        <Box overflow="auto" maxHeight={400}>
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
                                                    <IconButton
                                                        aria-label="delete"
                                                        onClick={() => handleDeleteMod(mod.id)}>
                                                        <DeleteIcon color="error"/>
                                                    </IconButton>
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
                                disabled={serverRunning}>Confirm</Button>
                        <Button onClick={handleClose} variant="outlined" color="error">Cancel</Button>
                    </Stack>
                </Box>
            </Modal>
        </>
    )
}

export default ReforgerModEdit;
