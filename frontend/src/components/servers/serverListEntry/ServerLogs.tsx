import {useEffect, useRef, useState} from "react";
import {Box, Modal, TextField, Typography} from "@mui/material";
import {downloadLogFile, getServerLogs} from "../../../services/serverLogService.ts";
import IconButton from "@mui/material/IconButton";
import FileDownloadIcon from '@mui/icons-material/FileDownload';
import RefreshIcon from '@mui/icons-material/Refresh';

const modalStyle = {
    position: 'absolute',
    top: '50%',
    left: '50%',
    transform: 'translate(-50%, -50%)',
    width: 800,
    bgcolor: 'background.paper',
    border: '2px solid #000',
    boxShadow: 24,
    p: 4,
};

type ServerLogsProps = {
    serverId: number,
    onClose: () => void
}

const ServerLogs = ({serverId, onClose}: ServerLogsProps) => {
    const [logs, setLogs] = useState("");
    const logTextArea = useRef<HTMLTextAreaElement>(null);

    useEffect(() => {
        void fetchLogs();
    }, []);

    async function fetchLogs() {
        const {data: downloadedLogs} = await getServerLogs(serverId);
        setLogs(downloadedLogs);
        if (logTextArea.current) {
            logTextArea.current.scrollTop = logTextArea.current.scrollHeight;
        }
    }

    async function handleDownloadLogFile() {
        downloadLogFile(serverId);
    }

    function isLogEmpty() {
        return !logs || logs.length === 0;
    }

    return (
        <Modal open onClose={onClose}>
            <Box sx={modalStyle}>
                {isLogEmpty() ?
                    <Typography m={2}>No logs available for this server</Typography>
                    :
                    <TextField multiline value={logs} disabled fullWidth rows={25}/>
                }
                <IconButton color="primary" aria-label="refresh logs" component="label" onClick={fetchLogs}>
                    <RefreshIcon/>
                </IconButton>
                <IconButton color="primary" aria-label="download log file" component="label"
                            onClick={handleDownloadLogFile} disabled={isLogEmpty()}
                >
                    <FileDownloadIcon/>
                </IconButton>
            </Box>
        </Modal>
    );
}

export default ServerLogs