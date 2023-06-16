import {useState} from "react";
import {Backdrop, Box, Button, CircularProgress, Modal} from "@mui/material";
import ListBuilder from "../../UI/ListBuilder/ListBuilder";
import {getServer, updateServer} from "../../services/serversService";
import {getMods} from "../../services/modsService";
import {toast} from "material-react-toastify";
import ApartmentIcon from '@mui/icons-material/Apartment';
import {ServerDto} from "../../dtos/ServerDto";

const ListBuilderDLCsEdit = props => {
    const [server, setServer] = useState<ServerDto>();
    const [isOpen, setIsOpen] = useState(false);
    const [availableDLCs, setAvailableDLCs] = useState([]);
    const [selectedDLCs, setSelectedDLCs] = useState([]);
    const [isLoading, setIsLoading] = useState(false);

    const serverRunning = props.server.instanceInfo && props.server.instanceInfo.alive;

    async function handleManageDLCsButtonClick() {
        setIsLoading(true);
        setIsOpen(false);
        try {
            const {data: serverDto} = await getServer(props.server.id);
            const {data: dlcsDto} = await getMods(props.server.type);
            setServer(serverDto);
            setSelectedDLCs(serverDto.activeDLCs);
            setAvailableDLCs(dlcsDto.creatorDlcs.filter(mod => !serverDto.activeDLCs.find(searchedDlc => searchedDlc.id === mod.id))
                .sort((a, b) => a.name.localeCompare(b.name)));
            setIsOpen(true);
        } catch (e) {
            console.error(e);
            toast.error(e.response.data || "Could not load server data");
        }
        setIsLoading(false);
    }

    function handleDLCSelect(option) {
        setAvailableDLCs((prevState) => {
            return prevState.filter(item => item !== option);
        });

        setSelectedDLCs((prevState) => {
            return [option, ...prevState].sort((a, b) => a.name.localeCompare(b.name));
        });
    }

    function handleDLCDeselect(option) {
        setSelectedDLCs((prevState) => {
            return prevState.filter(item => item !== option);
        });

        setAvailableDLCs((prevState) => {
            return [option, ...prevState].sort((a, b) => a.name.localeCompare(b.name));
        });
    }

    async function handleConfirm() {
        setIsOpen(false);
        try {
            await updateServer(props.server.id, {...server, activeDLCs: selectedDLCs});
            toast.success("DLCs successfully set");
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
            <Button onClick={handleManageDLCsButtonClick} startIcon={<ApartmentIcon/>}>
                DLCs
            </Button>
            <Modal open={isOpen} onClose={handleClose}>
                <Box>
                    <ListBuilder selectedOptions={selectedDLCs} availableOptions={availableDLCs}
                                 onSelect={handleDLCSelect} onDeselect={handleDLCDeselect}
                                 itemsLabel="DLCs" withControls
                                 onConfirm={handleConfirm} onCancel={handleClose}
                                 confirmDisabled={serverRunning}
                    />
                </Box>
            </Modal>
        </>
    )
}

export default ListBuilderDLCsEdit;
