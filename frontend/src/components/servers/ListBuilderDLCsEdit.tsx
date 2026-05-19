import {useEffect, useState} from "react";
import {Backdrop, Box, Button, CircularProgress, Modal} from "@mui/material";
import ListBuilder from "../../UI/ListBuilder/ListBuilder";
import {serversApi} from "../../api/client";
import {CreatorDlcDto, ServerDto, ServerInstanceInfoDto, ServerType} from "../../api/generated";
import {Arma3ServerDto} from "../../api/serverModels";
import {toast} from "react-toastify";
import ApartmentIcon from '@mui/icons-material/Apartment';
import {useServer} from "../../hooks/queries/useServer";
import {useCreatorDlcs} from "../../hooks/queries/useCreatorDlcs";
import {useQueryClient} from "@tanstack/react-query";
import {queryKeys} from "../../api/queryKeys";

type ListBuilderDLCsEditProps = {
    server: ServerDto,
    status: ServerInstanceInfoDto | null
}

const ListBuilderDLCsEdit = (props: ListBuilderDLCsEditProps) => {
    const queryClient = useQueryClient();
    const [isOpen, setIsOpen] = useState(false);
    const [availableDLCs, setAvailableDLCs] = useState<Array<CreatorDlcDto>>([]);
    const [selectedDLCs, setSelectedDLCs] = useState<Array<CreatorDlcDto>>([]);

    const {data: serverData, isLoading: serverLoading} = useServer(props.server.id, {enabled: isOpen});
    const {data: dlcsData = [], isLoading: dlcsLoading} = useCreatorDlcs(props.server.type as ServerType, {enabled: isOpen});

    const isLoading = isOpen && (serverLoading || dlcsLoading);
    const serverRunning = props.status != null && props.status.alive;

    useEffect(() => {
        if (!isOpen || !serverData || dlcsLoading) return;
        const activeDLCs: CreatorDlcDto[] = (serverData as Arma3ServerDto).activeDLCs ?? [];
        setSelectedDLCs(activeDLCs);
        setAvailableDLCs(dlcsData.filter(dlc => !activeDLCs.find(d => d.id === dlc.id)));
    }, [isOpen, serverData, dlcsLoading]);

    function handleManageDLCsButtonClick() {
        if (props.server.id === undefined) return;
        setIsOpen(true);
    }

    function handleDLCSelect(option: CreatorDlcDto) {
        setAvailableDLCs(prev => prev.filter(item => item !== option));
        setSelectedDLCs(prev => [option, ...prev].sort((a, b) => (a.name ?? "").localeCompare(b.name ?? "")));
    }

    function handleDLCDeselect(option: CreatorDlcDto) {
        setSelectedDLCs(prev => prev.filter(item => item !== option));
        setAvailableDLCs(prev => [option, ...prev].sort((a, b) => (a.name ?? "").localeCompare(b.name ?? "")));
    }

    function handleDLCReorder(items: CreatorDlcDto[]) {
        setSelectedDLCs(items);
    }

    function handleSelectAll() {
        setSelectedDLCs(prev => [...prev, ...availableDLCs].sort((a, b) => (a.name ?? "").localeCompare(b.name ?? "")));
        setAvailableDLCs([]);
    }

    function handleClearAll() {
        setAvailableDLCs(prev => [...prev, ...selectedDLCs].sort((a, b) => (a.name ?? "").localeCompare(b.name ?? "")));
        setSelectedDLCs([]);
    }

    async function handleConfirm() {
        if (props.server.id === undefined) return;

        setIsOpen(false);
        try {
            await serversApi.updateServer({
                id: props.server.id,
                serverDto: {...serverData, activeDLCs: selectedDLCs} as unknown as ServerDto
            });
            toast.success("DLCs successfully set");
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
            <Backdrop open={isLoading}>
                <CircularProgress color="inherit"/>
            </Backdrop>
            <Button onClick={handleManageDLCsButtonClick} startIcon={<ApartmentIcon/>} variant="contained">
                DLCs
            </Button>
            <Modal open={isOpen && !isLoading} onClose={handleClose}>
                <Box>
                    <ListBuilder selectedOptions={selectedDLCs} availableOptions={availableDLCs}
                                 onSelect={handleDLCSelect} onDeselect={handleDLCDeselect}
                                 onReorder={handleDLCReorder} onSelectAll={handleSelectAll} onClearAll={handleClearAll}
                                 itemsLabel="DLCs" withControls showFilter
                                 onConfirm={handleConfirm} onCancel={handleClose}
                                 confirmDisabled={serverRunning}
                    />
                </Box>
            </Modal>
        </>
    );
};

export default ListBuilderDLCsEdit;
