import {ChangeEvent, useState} from "react";
import {Box, Button, Modal, Tooltip} from "@mui/material";
import {ServerDto, ServerInstanceInfoDto} from "../../api/generated";
import {keysApi} from "../../api/client";
import {toast} from "react-toastify";
import {KeysTable} from "../keys/KeysTable";
import {useServerKeys} from "../../hooks/queries/useServerKeys";
import {useQueryClient} from "@tanstack/react-query";
import {queryKeys} from "../../api/queryKeys";
import KeyIcon from "@mui/icons-material/Key";

const modalStyle = {
    position: "absolute" as const,
    top: "50%",
    left: "50%",
    transform: "translate(-50%, -50%)",
    width: "80%",
    maxHeight: "90vh",
    overflow: "auto",
    bgcolor: "background.paper",
    boxShadow: 24,
    borderRadius: 1,
};

type Arma3BikeysControlProps = {
    server: ServerDto,
    status: ServerInstanceInfoDto | null
}

const Arma3BikeysControl = ({server, status}: Arma3BikeysControlProps) => {
    const queryClient = useQueryClient();
    const [isOpen, setIsOpen] = useState(false);
    const [selected, setSelected] = useState<Array<string>>([]);
    const [uploadInProgress, setUploadInProgress] = useState(false);

    const serverId = server.id as number;
    const serverRunning = status?.alive ?? false;

    const {data: keys = []} = useServerKeys(serverId, {enabled: isOpen});

    const handleOpen = () => {
        if (server.id === undefined) return;
        setIsOpen(true);
    };

    const handleClose = () => {
        setIsOpen(false);
        setSelected([]);
    };

    const handleFileChange = async (e: ChangeEvent<HTMLInputElement>) => {
        e.preventDefault();
        if (!e.target.files) return;

        try {
            setUploadInProgress(true);
            await keysApi.uploadServerKeys({id: serverId, file: Array.from(e.target.files)});
            toast.success("Bikey(s) successfully uploaded");
            await queryClient.invalidateQueries({queryKey: queryKeys.serverKeys(serverId)});
        } catch (ex: any) {
            console.error(ex);
        }
        setUploadInProgress(false);
    };

    const handleDelete = async () => {
        try {
            for (const name of selected) {
                await keysApi.deleteServerKey({id: serverId, name});
            }
            toast.success("Bikey(s) deleted successfully");
            setSelected([]);
            await queryClient.invalidateQueries({queryKey: queryKeys.serverKeys(serverId)});
        } catch (e) {
            console.error(e);
            toast.error("Error deleting bikeys");
        }
    };

    const handleSelectAllClick = (event: ChangeEvent<HTMLInputElement>) => {
        if (event.target.checked) {
            setSelected(keys.map((k) => k.name ?? "").filter(Boolean));
            return;
        }
        setSelected([]);
    };

    const handleRowClick = (id: string | number) => {
        if (typeof id !== "string") return;

        const selectedIndex = selected.indexOf(id);
        let newSelected: Array<string> = [];

        if (selectedIndex === -1) {
            newSelected = newSelected.concat(selected, id);
        } else if (selectedIndex === 0) {
            newSelected = newSelected.concat(selected.slice(1));
        } else if (selectedIndex === selected.length - 1) {
            newSelected = newSelected.concat(selected.slice(0, -1));
        } else if (selectedIndex > 0) {
            newSelected = newSelected.concat(
                selected.slice(0, selectedIndex),
                selected.slice(selectedIndex + 1),
            );
        }

        setSelected(newSelected);
    };

    return (
        <>
            <Tooltip title={serverRunning ? "Changes apply on next server start" : ""}>
                <Button onClick={handleOpen} startIcon={<KeyIcon/>} variant="outlined">
                    Bikeys
                </Button>
            </Tooltip>
            <Modal open={isOpen} onClose={handleClose}>
                <Box sx={modalStyle}>
                    <KeysTable
                        rows={keys}
                        selectedKeyIds={selected}
                        onSelectAllRowsClick={handleSelectAllClick}
                        onRowClick={handleRowClick}
                        onDeleteClicked={handleDelete}
                        onFileChange={handleFileChange}
                        uploadInProgress={uploadInProgress}
                        disabled={serverRunning}
                    />
                </Box>
            </Modal>
        </>
    );
};

export default Arma3BikeysControl;
