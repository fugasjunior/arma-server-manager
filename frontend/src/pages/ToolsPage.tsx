import {Tab, Tabs, Typography} from "@mui/material";
import {SyntheticEvent, useState} from "react";
import {SteamCmdLogsTool} from "../components/tools/SteamCmdLogsTool.tsx";

enum Tool {
    STEAMCMD_LOGS = "SteamCMD logs"
}

const ToolsPage = () => {
    const [selectedTool, setSelectedTool] = useState<Tool>(Tool.STEAMCMD_LOGS);

    const handleChange = (_: SyntheticEvent, newTool: Tool) => {
        setSelectedTool(newTool);
    };

    const renderSelectedTool = () => {
        switch (selectedTool) {
            case Tool.STEAMCMD_LOGS:
                return <SteamCmdLogsTool/>;
        }
    }

    return (
        <>
            <Typography variant="h4" component="h2" mb={2}>Tools</Typography>
            <Tabs value={renderSelectedTool} onChange={handleChange}>
                <Tab value={Tool.STEAMCMD_LOGS} label={Tool.STEAMCMD_LOGS} />
            </Tabs>
            {renderSelectedTool()}
        </>
    );
}

export default ToolsPage;