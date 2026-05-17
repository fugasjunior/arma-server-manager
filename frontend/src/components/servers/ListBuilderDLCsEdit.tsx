import {useState} from "react";
import {Backdrop, Box, Button, CircularProgress, Modal} from "@mui/material";
import ListBuilder from "../../UI/ListBuilder/ListBuilder";
import {serversApi, modsApi} from "../../api/client";
import {CreatorDlcDto, ServerDto, ServerInstanceInfoDto, ServerType} from "../../api/generated";
import {Arma3ServerDto} from "../../api/serverModels";
import {toast} from "react-toastify";
import ApartmentIcon from '@mui/icons-material/Apartment';

type ListBuilderDLCsEditProps = {
    server: ServerDto,
    status: ServerInstanceInfoDto | null
}

const ListBuilderDLCsEdit = (props: ListBuilderDLCsEditProps) => {
    const [server, setServer] = useState<ServerDto>();
    const [isOpen, setIsOpen] = useState(false);
    const [availableDLCs, setAvailableDLCs] = useState<Array<CreatorDlcDto>>([]);
    const [selectedDLCs, setSelectedDLCs] = useState<Array<CreatorDlcDto>>([]);
    const [isLoading, setIsLoading] = useState(false);

    const serverRunning = props.status != null && props.status.alive;

    async function handleManageDLCsButtonClick() {
        if (props.server.id === undefined) {
            return;
        }

        setIsLoading(true);
        setIsOpen(false);
        try {
            const {data: serverDto} = await serversApi.getServer({id: props.server.id});
            const {data: dlcsDto} = await modsApi.getMods({filter: props.server.type as ServerType});
            setServer(serverDto);
            setSelectedDLCs((serverDto as Arma3ServerDto).activeDLCs ?? []);
            setAvailableDLCs((dlcsDto.creatorDlcs ?? []).filter((mod: CreatorDlcDto) => !((serverDto as Arma3ServerDto).activeDLCs ?? []).find((searchedDlc: CreatorDlcDto) => searchedDlc.id === mod.id))
                .sort((a: CreatorDlcDto, b: CreatorDlcDto) => (a.name ?? "").localeCompare(b.name ?? "")));
            setIsOpen(true);
        } catch (e: any) {
            console.error(e);
            toast.error(e.response.data || "Could not load server data");
        }
        setIsLoading(false);
    }

    function handleDLCSelect(option: CreatorDlcDto) {
        setAvailableDLCs((prevState) => {
            return prevState.filter(item => item !== option);
        });

        setSelectedDLCs((prevState) => {
            return [option, ...prevState].sort((a, b) => (a.name ?? "").localeCompare(b.name ?? ""));
        });
    }

    function handleDLCDeselect(option: CreatorDlcDto) {
        setSelectedDLCs((prevState) => {
            return prevState.filter(item => item !== option);
        });

        setAvailableDLCs((prevState) => {
            return [option, ...prevState].sort((a, b) => (a.name ?? "").localeCompare(b.name ?? ""));
        });
    }

    async function handleConfirm() {
        if (props.server.id === undefined) {
            return;
        }

        setIsOpen(false);
        try {
            await serversApi.updateServer({id: props.server.id, serverDto: {...server, activeDLCs: selectedDLCs} as unknown as ServerDto});
            toast.success("DLCs successfully set");
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
            <Button onClick={handleManageDLCsButtonClick} startIcon={<ApartmentIcon/>} variant="contained">
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
