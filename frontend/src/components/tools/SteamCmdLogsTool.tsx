import {useRef} from "react";
import IconButton from "@mui/material/IconButton";
import RefreshIcon from "@mui/icons-material/Refresh";
import FileDownloadIcon from "@mui/icons-material/FileDownload";

export const SteamCmdLogsTool = () => {
    const logTextArea = useRef<HTMLTextAreaElement>(null);

    return (
        <div>
        <textarea value="test test test" disabled style={{"width": "100%", "height": "50vh", "resize": "none"}}
                  ref={logTextArea}/>
            <IconButton color="primary" aria-label="refresh logs" component="label">
                <RefreshIcon/>
            </IconButton>
            <IconButton color="primary" aria-label="download log file" component="label">
                <FileDownloadIcon/>
            </IconButton>
        </div>
    );
};