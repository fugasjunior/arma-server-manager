import React, {useState} from "react";
import {Tab, Tabs} from "@mui/material";
import ModsManagement from "../components/mods/ModsManagement";
import PresetsManagement from "../components/mods/PresetsManagement";

const ModsPage = () => {
    const [selectedTab, setSelectedTab] = useState("MODS");

    const handleTabSelect = (e, newValue) => {
        setSelectedTab(newValue);
    }

    return (
            <>
                <Tabs centered value={selectedTab} onChange={handleTabSelect}>
                    <Tab value="MODS" label="Mod management"/>
                    <Tab value="PRESETS" label="Preset management"/>
                </Tabs>
                {selectedTab === "MODS" && <ModsManagement/>}
                {selectedTab === "PRESETS" && <PresetsManagement/>}
            </>
    )
}

export default ModsPage;