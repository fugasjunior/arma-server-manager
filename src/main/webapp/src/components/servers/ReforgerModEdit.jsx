import React, {useState} from "react";
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

const ReforgerModEdit = props => {
    const [server, setServer] = useState();
    const [isOpen, setIsOpen] = useState(false);
    const [mods, setMods] = useState(props.server.activeMods);
    const [newModName, setNewModName] = useState("");
    const [newModId, setNewModId] = useState("");
    const [isLoading, setIsLoading] = useState(false);

    async function handleManageModsButtonClick() {
        setIsLoading(true);
        setIsOpen(false);
        try {
            const {data: serverDto} = await getServer(props.server.id);
            setServer(serverDto);
            setMods(serverDto.activeMods);
            setIsOpen(true);
        } catch (e) {
            console.error(e);
            toast.error(e.response.data || "Could not load server data");
        }
        setIsLoading(false);
    }

    function handleNewModNameChange(event) {
        setNewModName(event.target.value);
    }

    function handleNewModIdChange(event) {
        setNewModId(event.target.value);
    }

    function handleAddNewMod() {
        if (!mods.find(mod => mod.id === newModId)) {
            setMods(prevState => [...prevState, {id: newModId, name: newModName}]);
        }
        setNewModId("");
        setNewModName("");
    }

    function handleDeleteMod(id) {
        setMods(prevState => prevState.filter(mod => mod.id !== id));
    }

    async function handleConfirm() {
        setIsOpen(false);
        try {
            await updateServer(props.server.id, {...server, activeMods: mods});
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
                        <Button variant="contained" startIcon={<AddIcon/>}
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
                            <Stack>
                                <Button onClick={handleConfirm}>Confirm</Button>
                                <Button onClick={handleClose}>Cancel</Button>
                            </Stack>
                        </Box>
                    }
                </Box>
            </Modal>
        </>
    )
}

export default ReforgerModEdit;
