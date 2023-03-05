import React, {useEffect, useRef, useState} from "react";
import {Box, Modal, Typography} from "@mui/material";
import {downloadLogFile, getServerLogs} from "../../services/serverLogService";
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

const ServerLogs = (props) => {
    const [logs, setLogs] = useState("");
    let logTextArea = useRef();

    useEffect(() => {
        fetchLogs();
    }, []);

    async function fetchLogs() {
        const {data: downloadedLogs} = await getServerLogs(props.serverId);
        setLogs(downloadedLogs);
        logTextArea.current.scrollTop = logTextArea.current.scrollHeight;
    }

    async function handleDownloadLogFile() {
        downloadLogFile(props.serverId);
    }

    function isLogEmpty() {
        return !logs || logs.length === 0;
    }

    return (
        <Modal open onClose={props.onClose}>
            <Box sx={modalStyle}>
                {isLogEmpty() ?
                    <Typography m={2}>No logs available for this server</Typography>
                    :
                    <textarea value={logs} disabled style={{"width": "100%", "height": "80vh", "resize": "none"}} ref={logTextArea}/>
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