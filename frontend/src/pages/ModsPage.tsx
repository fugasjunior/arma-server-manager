import {Tab, Tabs} from "@mui/material";
import ModsManagement from "../components/mods/ModsManagement";
import PresetsManagement from "../components/mods/PresetsManagement";
import LocalModsManagement from "../components/mods/LocalModsManagement";
import {useNavigate, useParams} from "react-router-dom";

const ModsPage = () => {
    const {section} = useParams();
    const navigate = useNavigate();

    const handleTabSelect = (_: any, newValue: string) => {
        navigate("/mods/" + newValue);
    }

    return (
        <>
            <Tabs centered value={section ?? "MODS"} onChange={handleTabSelect}>
                <Tab value="MODS" label="Workshop mods"/>
                <Tab value="PRESETS" label="Preset management"/>
                <Tab value="LOCAL" label="Local mods"/>
            </Tabs>
            {(!section || section === "MODS") && <ModsManagement/>}
            {section === "PRESETS" && <PresetsManagement/>}
            {section === "LOCAL" && <LocalModsManagement/>}
        </>
    )
}

export default ModsPage;