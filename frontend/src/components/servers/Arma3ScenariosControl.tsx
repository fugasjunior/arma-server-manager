import {ChangeEvent, useState} from "react";
import {Box, Button, Modal} from "@mui/material";
import {ServerDto, ServerInstanceInfoDto} from "../../api/generated";
import {scenariosApi} from "../../api/client";
import {downloadScenario} from "../../api/downloads";
import {toast} from "react-toastify";
import {ScenariosTable} from "../scenarios/ScenariosTable";
import {useServerScenarios} from "../../hooks/queries/useServerScenarios";
import {useQueryClient} from "@tanstack/react-query";
import {queryKeys} from "../../api/queryKeys";
import MapIcon from "@mui/icons-material/Map";

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

type Arma3ScenariosControlProps = {
    server: ServerDto,
    status: ServerInstanceInfoDto | null
}

const Arma3ScenariosControl = ({server, status}: Arma3ScenariosControlProps) => {
    const queryClient = useQueryClient();
    const [isOpen, setIsOpen] = useState(false);
    const [selected, setSelected] = useState<Array<string>>([]);
    const [uploadInProgress, setUploadInProgress] = useState(false);
    const [percentUploaded, setPercentUploaded] = useState(0);

    const serverId = server.id as number;
    const serverRunning = status?.alive ?? false;

    const {data: scenarios = []} = useServerScenarios(serverId, {enabled: isOpen});

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
            await scenariosApi.uploadServerScenarios({id: serverId, file: Array.from(e.target.files)});
            toast.success("Scenario(s) successfully uploaded");
            await queryClient.invalidateQueries({queryKey: queryKeys.serverScenarios(serverId)});
        } catch (ex: any) {
            console.error(ex);
            toast.error(ex.response?.data ?? "Error uploading scenarios");
        }
        setUploadInProgress(false);
        setPercentUploaded(0);
    };

    const handleDownload = async (name: string, event: Event) => {
        event.stopPropagation();
        try {
            downloadScenario(serverId, name);
        } catch (e) {
            console.error(e);
            toast.error("Error during scenario download");
        }
    };

    const handleDelete = async () => {
        try {
            for (const name of selected) {
                await scenariosApi.deleteServerScenario({id: serverId, name});
            }
            toast.success("Scenario(s) deleted successfully");
            setSelected([]);
            await queryClient.invalidateQueries({queryKey: queryKeys.serverScenarios(serverId)});
        } catch (e) {
            console.error(e);
            toast.error("Error deleting scenarios");
        }
    };

    const handleSelectAllClick = (event: ChangeEvent<HTMLInputElement>) => {
        if (event.target.checked) {
            setSelected(scenarios.map((s) => s.name ?? "").filter(Boolean));
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
            <Button onClick={handleOpen} startIcon={<MapIcon/>} variant="contained">
                Scenarios
            </Button>
            <Modal open={isOpen} onClose={handleClose}>
                <Box sx={modalStyle}>
                    <ScenariosTable
                        rows={scenarios}
                        selectedScenarioIds={selected}
                        onSelectAllRowsClick={handleSelectAllClick}
                        onRowClick={handleRowClick}
                        onDeleteClicked={handleDelete}
                        onFileChange={handleFileChange}
                        percentUploaded={percentUploaded}
                        uploadInProgress={uploadInProgress}
                        onDownloadClicked={handleDownload}
                        disabled={serverRunning}
                    />
                </Box>
            </Modal>
        </>
    );
};

export default Arma3ScenariosControl;
