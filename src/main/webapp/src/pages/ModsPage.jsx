import React from "react";
import {Tab, Tabs} from "@mui/material";
import ModsManagement from "../components/mods/ModsManagement";
import PresetsManagement from "../components/mods/PresetsManagement";
import {useNavigate, useParams} from "react-router-dom";

const ModsPage = () => {
    const {section} = useParams();
    const navigate = useNavigate();

    const handleTabSelect = (e, newValue) => {
        navigate("/mods/" + newValue);
    }

    return (
            <>
                <Tabs centered value={section ?? "MODS"} onChange={handleTabSelect}>
                    <Tab value="MODS" label="Mod management"/>
                    <Tab value="PRESETS" label="Preset management"/>
                </Tabs>
                {(!section || section === "MODS") && <ModsManagement/>}
                {section === "PRESETS" && <PresetsManagement/>}
            </>
    )
}

export default ModsPage;