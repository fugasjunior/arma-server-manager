import {Tab, Tabs, Typography} from "@mui/material";
import {SyntheticEvent, useState} from "react";
import {SteamCmdLogsTool} from "../components/tools/SteamCmdLogsTool.tsx";
import {ApplicationLogsTool} from "../components/tools/ApplicationLogsTool.tsx";

enum Tool {
    STEAMCMD_LOGS = "SteamCMD logs",
    APPLICATION_LOGS = "Application logs"
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
            case Tool.APPLICATION_LOGS:
                return <ApplicationLogsTool/>;
        }
    }

    return (
        <>
            <Typography variant="h4" component="h2" sx={{mb: 2}}>Tools</Typography>
            <Tabs value={selectedTool} onChange={handleChange}>
                <Tab value={Tool.STEAMCMD_LOGS} label={Tool.STEAMCMD_LOGS} />
                <Tab value={Tool.APPLICATION_LOGS} label={Tool.APPLICATION_LOGS} />
            </Tabs>
            {renderSelectedTool()}
        </>
    );
}

export default ToolsPage;