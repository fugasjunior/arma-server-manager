import {useEffect, useRef, useState} from "react";
import IconButton from "@mui/material/IconButton";
import RefreshIcon from "@mui/icons-material/Refresh";
import FileDownloadIcon from "@mui/icons-material/FileDownload";
import {applicationLogApi} from "../../api/client";
import {downloadApplicationLog} from "../../api/downloads";
import {TextField} from "@mui/material";

export const ApplicationLogsTool = () => {
    const [logs, setLogs] = useState("");
    const logTextArea = useRef<HTMLTextAreaElement>(null);

    useEffect(() => {
        void fetchLogs();
    }, []);

    async function fetchLogs() {
        const {data: downloadedLogs} = await applicationLogApi.getApplicationLog();
        setLogs(downloadedLogs);
        if (logTextArea.current) {
            logTextArea.current.scrollTop = logTextArea.current.scrollHeight;
        }
    }

    function isLogEmpty() {
        return !logs || logs.length === 0;
    }

    return (
        <div>
            <TextField multiline value={logs} disabled fullWidth rows={25}/>
            <IconButton color="primary" aria-label="refresh logs" component="label" onClick={fetchLogs}>
                <RefreshIcon/>
            </IconButton>
            <IconButton color="primary" aria-label="download log file" component="label"
                        onClick={downloadApplicationLog} disabled={isLogEmpty()}
            >
                <FileDownloadIcon/>
            </IconButton>
        </div>
    );
};
