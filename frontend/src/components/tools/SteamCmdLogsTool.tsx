import {useEffect, useRef, useState} from "react";
import IconButton from "@mui/material/IconButton";
import RefreshIcon from "@mui/icons-material/Refresh";
import FileDownloadIcon from "@mui/icons-material/FileDownload";
import {downloadLogFile, getLogs} from "../../services/steamCmdService.ts";

export const SteamCmdLogsTool = () => {
    const [logs, setLogs] = useState("");
    const logTextArea = useRef<HTMLTextAreaElement>(null);

    useEffect(() => {
        void fetchLogs();
    }, []);

    async function fetchLogs() {
        const {data: downloadedLogs} = await getLogs();
        setLogs(downloadedLogs);
        if (logTextArea.current) {
            logTextArea.current.scrollTop = logTextArea.current.scrollHeight;
        }
    }

    async function handleDownloadLogFile() {
        downloadLogFile();
    }

    function isLogEmpty() {
        return !logs || logs.length === 0;
    }

    return (
        <div>
             <textarea value={logs} disabled style={{"width": "100%", "height": "50vh", "resize": "none"}}
                       ref={logTextArea}/>
            <IconButton color="primary" aria-label="refresh logs" component="label" onClick={fetchLogs}>
                <RefreshIcon/>
            </IconButton>
            <IconButton color="primary" aria-label="download log file" component="label"
                        onClick={handleDownloadLogFile} disabled={isLogEmpty()}
            >
                <FileDownloadIcon/>
            </IconButton>
        </div>
    );
};